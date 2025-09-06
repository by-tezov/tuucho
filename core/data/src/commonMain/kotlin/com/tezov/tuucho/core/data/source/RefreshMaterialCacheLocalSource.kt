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
import com.tezov.tuucho.core.domain.tool.datetime.ExpirationDateTimeRectifier
import kotlinx.serialization.json.JsonObject
import kotlin.time.Clock
import kotlin.time.Instant

class RefreshMaterialCacheLocalSource(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val materialDatabaseSource: MaterialDatabaseSource,
    private val materialBreaker: MaterialBreaker,
    private val expirationDateTimeRectifier: ExpirationDateTimeRectifier,
) {

    suspend fun isCacheValid(url: String, remoteValidityKey: String?): Boolean {
        materialDatabaseSource.getValidity(url)?.let { validity ->
            return validity.key == remoteValidityKey &&
                    validity.expirationDateTime?.let { expirationDateTime ->
                        expirationDateTime >= Clock.System.now()
                    } ?: true
        }
        return false
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
                    var validityDateTime: Instant? = null
                    ttlScope?.let {
                        val strategy = ttlScope.strategy
                        when (strategy) {
                            PageSettingSchema.Ttl.Value.Strategy.transient -> {
                                validityDateTime =
                                    ttlScope.transientValue
                                        ?.let { expirationDateTimeRectifier.process(it) }
                                        ?.let { Instant.parse(it) }
                                // ttlScope.transientOption //TODO: "stale-while-revalidate / stale-if-error"
                            }

                            PageSettingSchema.Ttl.Value.Strategy.noStore -> {
                                validityDateTime = Clock.System.now()
                                //TODO: should be removed from table as soon as used.
                                // It would not really respect because if not used right away, it will seat in database...
                            }
                        }
                    }
                    VersioningEntity(
                        url = url,
                        validityKey = validityKey,
                        expirationDateTime = validityDateTime,
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
        materialDatabaseSource.getValidity(url).let {
            materialDatabaseSource.deleteAll(url)
        }
    }
}