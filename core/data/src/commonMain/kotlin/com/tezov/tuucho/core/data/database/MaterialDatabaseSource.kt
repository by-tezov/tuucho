package com.tezov.tuucho.core.data.database

import com.tezov.tuucho.core.data.database.dao.HookQueries
import com.tezov.tuucho.core.data.database.dao.JsonObjectQueries
import com.tezov.tuucho.core.data.database.entity.HookEntity
import com.tezov.tuucho.core.data.database.entity.JsonObjectEntity
import com.tezov.tuucho.core.data.database.entity.JsonObjectEntity.Table
import com.tezov.tuucho.core.data.database.type.Lifetime
import com.tezov.tuucho.core.data.database.type.Visibility
import com.tezov.tuucho.core.domain.business.jsonSchema._system.onScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.IdSchema
import kotlinx.serialization.json.JsonObject

interface MaterialDatabaseSourceProtocol {

    suspend fun deleteAll(url: String, table: Table)

    suspend fun getHookEntityOrNull(url: String): HookEntity?

    suspend fun getRootJsonObjectEntityOrNull(url: String): JsonObjectEntity?

    suspend fun getLifetimeOrNull(url: String): Lifetime?

    suspend fun getAllCommonRefOrNull(
        from: JsonObject,
        url: String,
        type: String,
    ): List<JsonObject>?

    suspend fun getAllRefOrNull(
        from: JsonObject,
        url: String,
        type: String,
        visibility: Visibility.Contextual,
    ): List<JsonObject>?

    suspend fun insert(entity: HookEntity)

    suspend fun insert(
        entity: JsonObjectEntity,
        table: Table
    ): Long

}

class MaterialDatabaseSource(
    private val transactionFactory: DatabaseTransactionFactory,
    private val hookQueries: HookQueries,
    private val jsonObjectQueries: JsonObjectQueries,
):MaterialDatabaseSourceProtocol {

    override suspend fun deleteAll(url: String, table: Table) {
        transactionFactory.transaction {
            hookQueries.delete(url)
            jsonObjectQueries.deleteAll(url, table)
        }
    }

    override suspend fun getHookEntityOrNull(url: String) = hookQueries.getOrNull(url = url)

    override suspend fun getRootJsonObjectEntityOrNull(url: String): JsonObjectEntity? {
        val versioning = hookQueries.getOrNull(url = url) ?: return null
        versioning.rootPrimaryKey ?: return null
        return jsonObjectQueries.getCommonOrNull(versioning.rootPrimaryKey)
    }

    override suspend fun getLifetimeOrNull(url: String): Lifetime? =
        hookQueries.getLifetimeOrNull(url)

    override suspend fun getAllCommonRefOrNull(
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

    override suspend fun getAllRefOrNull(
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

    override suspend fun insert(entity: HookEntity) = hookQueries.insert(entity)

    override suspend fun insert(
        entity: JsonObjectEntity,
        table: Table
    ) = transactionFactory.transactionWithResult {
        jsonObjectQueries.insert(entity, table)
    }

}