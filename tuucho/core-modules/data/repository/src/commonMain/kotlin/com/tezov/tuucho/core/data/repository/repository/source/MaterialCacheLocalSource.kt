package com.tezov.tuucho.core.data.repository.repository.source

import com.tezov.tuucho.core.data.repository.database.DatabaseTransactionFactory
import com.tezov.tuucho.core.data.repository.database.MaterialDatabaseSource
import com.tezov.tuucho.core.data.repository.database.entity.HookEntity
import com.tezov.tuucho.core.data.repository.database.entity.JsonObjectEntity
import com.tezov.tuucho.core.data.repository.database.entity.JsonObjectEntity.Table
import com.tezov.tuucho.core.data.repository.database.type.JsonLifetime
import com.tezov.tuucho.core.data.repository.database.type.JsonVisibility
import com.tezov.tuucho.core.data.repository.exception.DataException
import com.tezov.tuucho.core.data.repository.image.ImageDiskCache
import com.tezov.tuucho.core.data.repository.parser.assembler.material.MaterialAssembler
import com.tezov.tuucho.core.data.repository.parser.assembler.material._system.AssemblerProtocol
import com.tezov.tuucho.core.data.repository.parser.breaker.MaterialBreaker
import com.tezov.tuucho.core.data.repository.repository.source._system.JsonLifetimeResolver
import com.tezov.tuucho.core.domain.business.jsonSchema._system.onScope
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.IdSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.PageSettingSchema
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlin.time.Clock
import kotlin.time.Instant

internal class MaterialCacheLocalSource(
    private val transactionFactory: DatabaseTransactionFactory,
    private val materialDatabaseSource: MaterialDatabaseSource,
    private val imageDiskCache: ImageDiskCache,
    private val materialBreaker: MaterialBreaker,
    private val materialAssembler: MaterialAssembler,
    private val lifetimeResolver: JsonLifetimeResolver,
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
                is JsonLifetime.Unlimited, is JsonLifetime.SingleUse -> true
                is JsonLifetime.Transient -> lifetime.expirationDateTime >= now.invoke()
                is JsonLifetime.Enrolled -> false
            }
        }
        return false
    }

    @Suppress("RedundantSuspendModifier")
    suspend fun delete(
        url: String,
        table: Table
    ) {
        transactionFactory.transaction {
            materialDatabaseSource.run { deleteAll(url = url, table = table) }
            imageDiskCache.run { deleteAll(cacheKeyPrefix = url) }
        }
    }

    suspend fun insert(
        materialObject: JsonObject,
        url: String,
        urlWhiteList: JsonArray?,
        visibility: JsonVisibility,
        weakLifetime: JsonLifetime,
    ) {
        val nodes = materialBreaker.process(
            materialObject = materialObject,
        )
        with(nodes) {
            val table = if (visibility is JsonVisibility.Contextual) {
                Table.Contextual
            } else {
                Table.Common
            }
            val rootPrimaryKey = rootJsonObject?.let {
                materialDatabaseSource.insert(it.toEntity(url), table)
            }
            HookEntity(
                url = url,
                urlWhiteList = urlWhiteList,
                rootPrimaryKey = rootPrimaryKey,
                visibility = visibility,
                lifetime = lifetimeResolver.invoke(
                    timeToLiveObject = materialObject
                        .onScope(PageSettingSchema::Scope)
                        .timeToLive,
                    weakLifetime = weakLifetime,
                ),
            ).also { materialDatabaseSource.insert(it) }
            jsonObjects
                .forEach { materialDatabaseSource.insert(it.toEntity(url), table) }
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
        urlWhiteList: JsonArray?,
        validityKey: String,
        visibility: JsonVisibility,
    ) {
        HookEntity(
            url = url,
            urlWhiteList = urlWhiteList,
            rootPrimaryKey = null,
            visibility = visibility,
            lifetime = JsonLifetime.Enrolled(validityKey),
        ).also { materialDatabaseSource.insert(it) }
    }

    suspend fun getLifetime(
        url: String
    ) = materialDatabaseSource.getHookEntityOrNull(url)?.lifetime

    suspend fun assemble(
        url: String
    ): JsonObject? {
        val entity = materialDatabaseSource.getRootJsonObjectEntityOrNull(url) ?: return null
        return materialAssembler.process(
            context = AssemblerProtocol.Context(
                url = url,
                findAllRefOrNullFetcher = { from, type ->
                    materialDatabaseSource.getAllCommonRefOrNull(from, url, type)
                }
            ),
            materialObject = entity.jsonObject
        )
    }
}
