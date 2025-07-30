package com.tezov.tuucho.core.data.source

import com.tezov.tuucho.core.data.database.MaterialDatabaseSource
import com.tezov.tuucho.core.data.database.entity.JsonObjectEntity
import com.tezov.tuucho.core.data.database.entity.VersioningEntity
import com.tezov.tuucho.core.data.exception.DataException
import com.tezov.tuucho.core.data.parser._system.JsonEntityObjectTree
import com.tezov.tuucho.core.data.parser._system.flatten
import com.tezov.tuucho.core.data.parser._system.jsonEntityObjectTree
import com.tezov.tuucho.core.data.parser.breaker.MaterialBreaker
import com.tezov.tuucho.core.domain.model.schema._system.onScope
import com.tezov.tuucho.core.domain.model.schema._system.withScope
import com.tezov.tuucho.core.domain.model.schema.material.IdSchema
import com.tezov.tuucho.core.domain.model.schema.material.TypeSchema
import com.tezov.tuucho.core.domain.protocol.CoroutineScopeProviderProtocol
import kotlinx.coroutines.async
import kotlinx.serialization.json.JsonObject

class RefreshMaterialCacheLocalSource(
    private val coroutineScopeProvider: CoroutineScopeProviderProtocol,
    private val materialDatabaseSource: MaterialDatabaseSource,
    private val materialBreaker: MaterialBreaker
) {

    suspend fun shouldRefresh(/* TODO */): Boolean {
//TODO
//        return database.versioning().countVersions(url).let { result ->
//            result.any { it.version != version } || result.isEmpty()
//        }
        return true
    }

    suspend fun process(
        material: JsonObject,
        url: String,
        isShared: Boolean
    ) {
        //TODO auto purge obsolete entry
        val parts = coroutineScopeProvider.parser.async {
            materialBreaker.process(
                material = material,
                jsonEntityObjectTreeProducer = { jsonObject ->
                    val idScope = jsonObject.onScope(IdSchema::Scope)
                    JsonObjectEntity(
                        type = jsonObject.withScope(TypeSchema::Scope).self
                            ?: throw DataException.Default("Missing type, so there is surely something missing in the rectifier for $this"),
                        url = url,
                        id = idScope.value
                            ?: throw DataException.Default("Missing Id, so there is surely something missing in the rectifier for $this"),
                        idFrom = idScope.source,
                        jsonObject = jsonObject
                    ).let(::JsonEntityObjectTree)
                }
            )
        }.await()
        coroutineScopeProvider.database.async {
            with(parts) {
                val rootPrimaryKey = rootJsonEntity?.let { root ->
                    materialDatabaseSource.insertOrUpdate(root.jsonEntityObjectTree.content)
                        .also {
                            root.flatten()
                                .asSequence()
                                .filter { it !== root }
                                .forEach {
                                    materialDatabaseSource.insertOrUpdate(it.content)
                                }
                        }
                }

                VersioningEntity(
                    url = url,
                    version = version,
                    rootPrimaryKey = rootPrimaryKey,
                    isShared = isShared
                ).also { materialDatabaseSource.insertOrUpdate(it) }

                jsonElementTree
                    .asSequence()
                    .flatMap { it.flatten() }
                    .forEach {
                        materialDatabaseSource.insertOrUpdate(it.content)
                    }
            }
        }
    }
}