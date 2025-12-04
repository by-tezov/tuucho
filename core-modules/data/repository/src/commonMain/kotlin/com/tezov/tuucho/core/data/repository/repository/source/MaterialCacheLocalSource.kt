package com.tezov.tuucho.core.data.repository.repository.source

import com.tezov.tuucho.core.data.repository.database.MaterialDatabaseSource
import com.tezov.tuucho.core.data.repository.database.entity.HookEntity
import com.tezov.tuucho.core.data.repository.database.entity.JsonObjectEntity
import com.tezov.tuucho.core.data.repository.database.entity.JsonObjectEntity.Table
import com.tezov.tuucho.core.data.repository.database.type.Lifetime
import com.tezov.tuucho.core.data.repository.database.type.Visibility
import com.tezov.tuucho.core.data.repository.exception.DataException
import com.tezov.tuucho.core.data.repository.parser.assembler.material.MaterialAssembler
import com.tezov.tuucho.core.data.repository.parser.breaker.MaterialBreaker
import com.tezov.tuucho.core.data.repository.repository.source._system.LifetimeResolver
import com.tezov.tuucho.core.domain.business.jsonSchema._system.onScope
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.IdSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.MaterialSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlin.time.Clock
import kotlin.time.Instant

internal class MaterialCacheLocalSource(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val materialDatabaseSource: MaterialDatabaseSource,
    private val materialBreaker: MaterialBreaker,
    private val materialAssembler: MaterialAssembler,
    private val lifetimeResolver: LifetimeResolver,
) {
    suspend fun isCacheValid(
        url: String,
        remoteValidityKey: String?,
        now: () -> Instant = { Clock.System.now() },
    ): Boolean {
        materialDatabaseSource.getLifetimeOrNull(url)?.let { lifetime ->
            if (lifetime.validityKey != remoteValidityKey) {
                return false
            }
            return when (lifetime) {
                is Lifetime.Unlimited, is Lifetime.SingleUse -> true
                is Lifetime.Transient -> lifetime.expirationDateTime >= now.invoke()
                is Lifetime.Enrolled -> false
            }
        }
        return false
    }

    suspend fun delete(
        url: String,
        table: Table
    ) {
        materialDatabaseSource.deleteAll(url, table)
    }

    suspend fun insert(
        materialObject: JsonObject,
        url: String,
        visibility: Visibility,
        weakLifetime: Lifetime,
    ) {
        val nodes = coroutineScopes.parser.await {
            materialBreaker.process(
                materialObject = materialObject,
            )
        }
        coroutineScopes.database.await {
            with(nodes) {
                val table = if (visibility is Visibility.Contextual) {
                    Table.Contextual
                } else {
                    Table.Common
                }
                val rootPrimaryKey = rootJsonObject?.let {
                    materialDatabaseSource.insert(it.toEntity(url), table)
                }
                HookEntity(
                    url = url,
                    rootPrimaryKey = rootPrimaryKey,
                    visibility = visibility,
                    lifetime = lifetimeResolver.invoke(
                        pageSetting = materialObject.withScope(MaterialSchema::Scope).pageSetting,
                        weakLifetime = weakLifetime,
                    ),
                ).also { materialDatabaseSource.insert(it) }
                jsonObjects
                    .forEach { materialDatabaseSource.insert(it.toEntity(url), table) }
            }
        }
    }

    private fun JsonElement.toEntity(
        url: String,
    ): JsonObjectEntity {
        val idScope = onScope(IdSchema::Scope)
        return JsonObjectEntity(
            type = withScope(TypeSchema::Scope).self
                ?: throw DataException.Default("Missing type, so there is surely something missing in the rectifier for $this"),
            url = url,
            id = idScope.value
                ?: throw DataException.Default("Missing Id, so there is surely something missing in the rectifier for $this"),
            idFrom = idScope.source,
            jsonObject = jsonObject
        )
    }

    suspend fun enroll(
        url: String,
        validityKey: String,
        visibility: Visibility,
    ) {
        coroutineScopes.database
            .await {
                HookEntity(
                    url = url,
                    rootPrimaryKey = null,
                    visibility = visibility,
                    lifetime = Lifetime.Enrolled(validityKey),
                ).also { materialDatabaseSource.insert(it) }
            }
    }

    suspend fun getLifetime(
        url: String
    ) = coroutineScopes.database.await {
        materialDatabaseSource.getHookEntityOrNull(url)?.lifetime
    }

    suspend fun assemble(
        url: String
    ): JsonObject? {
        val entity = coroutineScopes.database.await {
            materialDatabaseSource.getRootJsonObjectEntityOrNull(url)
        } ?: return null
        return coroutineScopes.parser.await {
            materialAssembler.process(
                materialObject = entity.jsonObject,
                findAllRefOrNullFetcher = { from, type ->
                    coroutineScopes.database.await {
                        materialDatabaseSource.getAllCommonRefOrNull(from, url, type)
                    }
                }
            )
        }
    }
}
