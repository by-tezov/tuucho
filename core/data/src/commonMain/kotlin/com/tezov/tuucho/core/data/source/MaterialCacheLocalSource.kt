package com.tezov.tuucho.core.data.source

import com.tezov.tuucho.core.data.database.MaterialDatabaseSource
import com.tezov.tuucho.core.data.database.entity.JsonObjectEntity
import com.tezov.tuucho.core.data.database.entity.VersioningEntity
import com.tezov.tuucho.core.data.database.type.Lifetime
import com.tezov.tuucho.core.data.database.type.Visibility
import com.tezov.tuucho.core.data.exception.DataException
import com.tezov.tuucho.core.data.parser._system.JsonObjectEntityTree
import com.tezov.tuucho.core.data.parser._system.flatten
import com.tezov.tuucho.core.data.parser.assembler.MaterialAssembler
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

class MaterialCacheLocalSource(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val materialDatabaseSource: MaterialDatabaseSource,
    private val materialBreaker: MaterialBreaker,
    private val materialAssembler: MaterialAssembler,
    private val expirationDateTimeRectifier: ExpirationDateTimeRectifier,
) {

    suspend fun isCacheValid(url: String, remoteValidityKey: String?): Boolean {
        materialDatabaseSource.getLifetimeOrNull(url)?.let { lifetime ->
            if (lifetime.validityKey != remoteValidityKey) {
                return false
            }
            return when (lifetime) {
                is Lifetime.Unlimited -> true
                is Lifetime.Transient -> lifetime.expirationDateTime >= Clock.System.now().also {
                    println(lifetime.expirationDateTime >= Clock.System.now())
                }

                is Lifetime.Enrolled -> false
            }
        }
        return false
    }

    suspend fun delete(url: String) {
        materialDatabaseSource.deleteAll(url)
    }

    suspend fun insert(
        materialObject: JsonObject,
        url: String,
        weakLifetime: Lifetime,
        visibility: Visibility,
    ) {
        val parts = coroutineScopes.parser.await {
            materialBreaker.process(
                materialObject = materialObject,
                versioningEntityFactory = { pageSetting ->
                    val settingScope = pageSetting?.withScope(PageSettingSchema::Scope)
                    val ttlScope = settingScope?.ttl?.withScope(PageSettingSchema.Ttl::Scope)
                    val lifetime = if (ttlScope != null) {
                        val strategy = ttlScope.strategy
                        when (strategy) {
                            PageSettingSchema.Ttl.Value.Strategy.transient -> {
                                val expirationDateTime =
                                    ttlScope.transientValue
                                        ?.let { expirationDateTimeRectifier.process(it) }
                                        ?.let { Instant.parse(it) }
                                        ?: throw DataException.Default("ttl transient, missing property transient-value")
                                Lifetime.Transient(weakLifetime.validityKey, expirationDateTime)
                                // ttlScope.transientOption //TODO: "stale-while-revalidate / stale-if-error"
                            }

                            PageSettingSchema.Ttl.Value.Strategy.noStore -> {
                                val expirationDateTime = Clock.System.now()
                                Lifetime.Transient(weakLifetime.validityKey, expirationDateTime)
                                //TODO: should be removed from table as soon as used.
                                // It would not really respect because if not used right away, it will seat in database...
                            }

                            else -> throw DataException.Default("unknown ttl strategy $strategy")
                        }
                    } else null
                    VersioningEntity(
                        url = url,
                        rootPrimaryKey = null,
                        visibility = visibility,
                        lifetime = lifetime ?: weakLifetime,
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
                    materialDatabaseSource.insert(root)
                }
                versionEntity.copy(
                    rootPrimaryKey = rootPrimaryKey,
                ).also { materialDatabaseSource.insertOrUpdate(it) }
                jsonElementTree
                    .asSequence()
                    .flatMap { it.flatten() }
                    .forEach {
                        materialDatabaseSource.insert(it.content)
                    }

            }
        }
    }

    suspend fun enroll(
        url: String,
        validityKey: String,
        visibility: Visibility,
    ) {
        coroutineScopes.database.await {
            VersioningEntity(
                url = url,
                rootPrimaryKey = null,
                visibility = visibility,
                lifetime = Lifetime.Enrolled(validityKey),
            )
        }
    }

    suspend fun getLifetime(url: String) = coroutineScopes.database.await {
        materialDatabaseSource.getVersioningEntityOrNull(url)?.lifetime
    }

    suspend fun read(url: String): JsonObject? {
        val entity = coroutineScopes.database.await {
            materialDatabaseSource.getRootJsonObjectEntityOrNull(url)
        } ?: return null
        return coroutineScopes.parser.await {
            materialAssembler.process(
                materialObject = entity.jsonObject,
                findAllRefOrNullFetcher = { from, type ->
                    coroutineScopes.database.await {
                        materialDatabaseSource.getAllRefOrNull(from, url, type)
                    }
                }
            )
        }
    }

}