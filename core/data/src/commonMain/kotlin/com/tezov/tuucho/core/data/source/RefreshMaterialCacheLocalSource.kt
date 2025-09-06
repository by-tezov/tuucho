package com.tezov.tuucho.core.data.source

import com.tezov.tuucho.core.data.database.MaterialDatabaseSource
import com.tezov.tuucho.core.data.database.entity.JsonObjectEntity
import com.tezov.tuucho.core.data.database.entity.VersioningEntity
import com.tezov.tuucho.core.data.database.type.Lifetime
import com.tezov.tuucho.core.data.database.type.Visibility
import com.tezov.tuucho.core.data.exception.DataException
import com.tezov.tuucho.core.data.parser._system.JsonObjectEntityTree
import com.tezov.tuucho.core.data.parser._system.flatten
import com.tezov.tuucho.core.data.parser.breaker.MaterialBreaker
import com.tezov.tuucho.core.domain.business.jsonSchema._system.onScope
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.IdSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.page.PageSettingSchema
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.tool.datetime.ExpirationDateTimeParser
import kotlinx.serialization.json.JsonObject

class RefreshMaterialCacheLocalSource(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val materialDatabaseSource: MaterialDatabaseSource,
    private val materialBreaker: MaterialBreaker,
    private val expirationDateTimeParser: ExpirationDateTimeParser,
) {

    suspend fun shouldRefresh(url: String, remoteValidityKey: String): Boolean {
        materialDatabaseSource.getValidityKey(url)?.let {
            return it != remoteValidityKey
        }
        return true
    }

    suspend fun process(
        materialObject: JsonObject,
        url: String,
        validityKey: String?,
        visibility: Visibility,
        lifetime: Lifetime,
    ) {
        purgeCache(url)
        val parts = coroutineScopes.parser.await {
            materialBreaker.process(
                materialObject = materialObject,
                versioningEntityFactory = { pageSetting ->
                    val settingScope = pageSetting?.withScope(PageSettingSchema::Scope)
                    val ttlScope = settingScope?.ttl?.withScope(PageSettingSchema.Ttl::Scope)

                    ttlScope?.let {
//                        println(it.strategy)
//                        println(it.transientValue)
//                        println(it.transientOption)

                        if(url == "page-home") {
                            println(expirationDateTimeParser.parse("16:00"))
                        }


                    }

                    VersioningEntity(
                        url = url,
                        validityKey = validityKey,
                        validityDateTime = null,
                        validityTimeZone = null,
                        rootPrimaryKey = null,
                        visibility = visibility,
                        lifetime = lifetime,
                    )
                },
                jsonObjectEntityTreeProducer = { jsonObject ->
                    val idScope = jsonObject.onScope(IdSchema::Scope)
                    JsonObjectEntity(
                        type = jsonObject.withScope(TypeSchema::Scope).self
                            ?: throw DataException.Default("Missing type, so there is surely something missing in the rectifier for $this"),
                        url = url,
                        id = idScope.value
                            ?: throw DataException.Default("Missing Id, so there is surely something missing in the rectifier for $this"),
                        idFrom = idScope.source,
                        jsonObject = jsonObject
                    ).let(::JsonObjectEntityTree)
                }
            )
        }
        coroutineScopes.database.await {
            with(parts) {
                val rootPrimaryKey = rootJsonObjectEntity?.let { root ->
                    materialDatabaseSource
                        .insertOrUpdate(root, lifetime)
                }
                versionEntity.copy(
                    rootPrimaryKey = rootPrimaryKey,
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

    private suspend fun purgeCache(url: String) {
        materialDatabaseSource.getValidityKey(url).let {
            materialDatabaseSource.deleteAll(url)
        }
    }
}