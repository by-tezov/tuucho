package com.tezov.tuucho.core.data.repository.database

import com.tezov.tuucho.core.data.repository.database.dao.HookQueries
import com.tezov.tuucho.core.data.repository.database.dao.JsonObjectQueries
import com.tezov.tuucho.core.data.repository.database.entity.HookEntity
import com.tezov.tuucho.core.data.repository.database.entity.JsonObjectEntity
import com.tezov.tuucho.core.data.repository.database.entity.JsonObjectEntity.Table
import com.tezov.tuucho.core.data.repository.database.type.Lifetime
import com.tezov.tuucho.core.data.repository.database.type.Visibility
import com.tezov.tuucho.core.domain.business.jsonSchema._system.onScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.IdSchema
import com.tezov.tuucho.core.domain.test._system.OpenForTest
import kotlinx.serialization.json.JsonObject

@OpenForTest
internal class MaterialDatabaseSource(
    private val transactionFactory: DatabaseTransactionFactory,
    private val hookQueries: HookQueries,
    private val jsonObjectQueries: JsonObjectQueries,
) {
    @Suppress("RedundantSuspendModifier")
    suspend fun deleteAll(
        url: String,
        table: Table
    ) {
        transactionFactory.transaction {
            hookQueries.delete(url)
            jsonObjectQueries.deleteAll(url, table) // since delete cascade is active, this one is not needed
        }
    }

    @Suppress("RedundantSuspendModifier")
    suspend fun getHookEntityOrNull(
        url: String
    ) = hookQueries.getOrNull(url = url)

    @Suppress("RedundantSuspendModifier")
    suspend fun getRootJsonObjectEntityOrNull(
        url: String
    ): JsonObjectEntity? {
        val versioning = hookQueries.getOrNull(url = url) ?: return null
        versioning.rootPrimaryKey ?: return null
        return jsonObjectQueries.getCommonOrNull(versioning.rootPrimaryKey)
    }

    @Suppress("RedundantSuspendModifier")
    suspend fun getLifetimeOrNull(
        url: String
    ): Lifetime? = hookQueries.getLifetimeOrNull(url)

    @Suppress("RedundantSuspendModifier")
    suspend fun getAllCommonRefOrNull(
        from: JsonObject,
        url: String,
        type: String,
    ): List<JsonObject>? {
        from.onScope(IdSchema::Scope).source ?: return null
        return buildList {
            var currentEntry = from
            add(currentEntry)
            do {
                val idRef = currentEntry.onScope(IdSchema::Scope).source
                val entity = idRef?.let { ref ->
                    jsonObjectQueries.getCommonOrNull(type = type, url = url, id = ref)
                        ?: jsonObjectQueries.getCommonGlobalOrNull(type = type, id = ref)
                }
                if (entity != null) {
                    currentEntry = entity.jsonObject
                    add(currentEntry)
                }
            } while (idRef != null && entity != null)
        }
    }

    @Suppress("RedundantSuspendModifier")
    suspend fun getAllRefOrNull(
        from: JsonObject,
        url: String,
        type: String,
        visibility: Visibility.Contextual,
    ): List<JsonObject>? {
        from.onScope(IdSchema::Scope).source ?: return null
        return buildList {
            var currentEntry = from
            add(currentEntry)
            do {
                val idRef = currentEntry.onScope(IdSchema::Scope).source
                val entity = idRef?.let { ref ->
                    jsonObjectQueries.getContextualOrNull(type = type, url = url, id = ref, visibility = visibility)
                        ?: jsonObjectQueries.getCommonOrNull(type = type, url = visibility.urlOrigin, id = ref)
                        ?: jsonObjectQueries.getCommonGlobalOrNull(type = type, id = ref)
                }
                if (entity != null) {
                    currentEntry = entity.jsonObject
                    add(currentEntry)
                }
            } while (idRef != null && entity != null)
        }
    }

    @Suppress("RedundantSuspendModifier")
    suspend fun insert(
        entity: HookEntity
    ) = hookQueries.insert(entity)

    @Suppress("RedundantSuspendModifier")
    suspend fun insert(
        entity: JsonObjectEntity,
        table: Table
    ) = transactionFactory.transactionWithResult {
        jsonObjectQueries.insert(entity, table)
    }
}
