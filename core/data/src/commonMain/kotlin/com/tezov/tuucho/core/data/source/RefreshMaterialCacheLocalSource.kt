package com.tezov.tuucho.core.data.source

import com.tezov.tuucho.core.data.database.MaterialDatabaseSource
import com.tezov.tuucho.core.data.database.entity.JsonObjectEntity
import com.tezov.tuucho.core.data.database.entity.VersioningEntity
import com.tezov.tuucho.core.data.database.type.Lifetime
import com.tezov.tuucho.core.data.database.type.Visibility
import com.tezov.tuucho.core.data.exception.DataException
import com.tezov.tuucho.core.data.parser._system.JsonEntityObjectTree
import com.tezov.tuucho.core.data.parser._system.flatten
import com.tezov.tuucho.core.data.parser._system.jsonEntityObjectTree
import com.tezov.tuucho.core.data.parser.breaker.MaterialBreaker
import com.tezov.tuucho.core.domain.business.model.schema._system.onScope
import com.tezov.tuucho.core.domain.business.model.schema._system.withScope
import com.tezov.tuucho.core.domain.business.model.schema.material.IdSchema
import com.tezov.tuucho.core.domain.business.model.schema.material.TypeSchema
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import kotlinx.serialization.json.JsonObject

class RefreshMaterialCacheLocalSource(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val materialDatabaseSource: MaterialDatabaseSource,
    private val materialBreaker: MaterialBreaker,
) {

    suspend fun shouldRefresh(/* TODO */): Boolean {
//TODO
//        return database.versioning().countVersions(url).let { result ->
//            result.any { it.version != version } || result.isEmpty()
//        }
        return true
    }

    suspend fun process(
        materialObject: JsonObject,
        url: String,
        visibility: Visibility,
        lifetime: Lifetime,
    ) {
        //TODO auto purge obsolete entry
        val parts = coroutineScopes.parser.on {
            materialBreaker.process(
                materialObject = materialObject,
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
        }
        coroutineScopes.database.on {
            with(parts) {
                val rootPrimaryKey = rootJsonEntity?.let { root ->
                    materialDatabaseSource
                        .insertOrUpdate(root.jsonEntityObjectTree.content, lifetime)
                        .also {
                            root.flatten()
                                .asSequence()
                                .filter { it !== root }
                                .forEach {
                                    materialDatabaseSource
                                        .insertOrUpdate(it.content, lifetime)
                                }
                        }
                }

                VersioningEntity(
                    url = url,
                    version = version,
                    rootPrimaryKey = rootPrimaryKey,
                    visibility = visibility,
                    lifetime = lifetime,
                ).also { materialDatabaseSource.insertOrUpdate(it) }

                jsonElementTree
                    .asSequence()
                    .flatMap { it.flatten() }
                    .forEach {
                        materialDatabaseSource.insertOrUpdate(it.content, lifetime)
                    }
            }
        }
    }
}