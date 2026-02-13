package com.tezov.tuucho.core.data.repository.database

import app.cash.sqldelight.TransactionWithoutReturn
import com.tezov.tuucho.core.data.repository.database.dao.HookQueries
import com.tezov.tuucho.core.data.repository.database.dao.JsonObjectQueries
import com.tezov.tuucho.core.data.repository.database.entity.HookEntity
import com.tezov.tuucho.core.data.repository.database.entity.JsonObjectEntity
import com.tezov.tuucho.core.data.repository.database.entity.JsonObjectEntity.Table
import com.tezov.tuucho.core.data.repository.database.type.JsonVisibility
import com.tezov.tuucho.core.domain.business.jsonSchema._system.onScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.IdSchema
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.test._system.OpenForTest
import kotlinx.serialization.json.JsonObject

@OpenForTest
internal class MaterialDatabaseSource(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val databaseTransactionFactory: DatabaseTransactionFactory,
    private val hookQueries: HookQueries,
    private val jsonObjectQueries: JsonObjectQueries
) {
    suspend fun selectAllHooks() = coroutineScopes.io.withContext {
        databaseTransactionFactory.transactionWithResult {
            hookQueries.selectAll()
        }
    }

    suspend fun selectAllJsons(
        table: Table
    ) = coroutineScopes.io.withContext {
        databaseTransactionFactory.transactionWithResult {
            jsonObjectQueries.selectAll(table)
        }
    }

    fun TransactionWithoutReturn.deleteAll(
        url: String,
        table: Table
    ) {
        hookQueries.delete(url)
        jsonObjectQueries.deleteAll(url, table)
    }

    suspend fun getHookEntityOrNull(
        url: String
    ) = coroutineScopes.io.withContext {
        databaseTransactionFactory.transactionWithResult {
            hookQueries.getOrNull(url = url)
        }
    }

    suspend fun getRootJsonObjectEntityOrNull(
        url: String
    ) = coroutineScopes.io.withContext {
        databaseTransactionFactory.transactionWithResult {
            val versioning = hookQueries.getOrNull(url = url) ?: return@transactionWithResult null
            versioning.rootPrimaryKey ?: return@transactionWithResult null
            jsonObjectQueries.getCommonOrNull(versioning.rootPrimaryKey)
        }
    }

    suspend fun getLifetimeOrNull(
        url: String
    ) = coroutineScopes.io.withContext {
        databaseTransactionFactory.transactionWithResult {
            hookQueries.getLifetimeOrNull(url)
        }
    }

    suspend fun getAllCommonRefOrNull(
        from: JsonObject,
        url: String,
        type: String,
    ) = coroutineScopes.io.withContext {
        databaseTransactionFactory.transactionWithResult {
            from.onScope(IdSchema::Scope).source ?: return@transactionWithResult null
            buildList {
                var currentEntry = from
                add(currentEntry)
                do {
                    val idRef = currentEntry.onScope(IdSchema::Scope).source
                    val entity = idRef?.let { ref ->
                        jsonObjectQueries.getCommonOrNull(type = type, url = url, id = ref)
                            ?: jsonObjectQueries.getCommonGlobalOrNull(type = type, url = url, id = ref)
                    }
                    if (entity != null) {
                        currentEntry = entity.jsonObject
                        add(currentEntry)
                    }
                } while (idRef != null && entity != null)
            }
        }
    }

    suspend fun getAllRefOrNull(
        from: JsonObject,
        url: String,
        type: String,
        visibility: JsonVisibility.Contextual,
    ) = coroutineScopes.io.withContext {
        databaseTransactionFactory.transactionWithResult {
            from.onScope(IdSchema::Scope).source ?: return@transactionWithResult null
            buildList {
                var currentEntry = from
                add(currentEntry)
                do {
                    val idRef = currentEntry.onScope(IdSchema::Scope).source
                    val entity = idRef?.let { ref ->
                        jsonObjectQueries.getContextualOrNull(type = type, url = url, id = ref, visibility = visibility)
                            ?: jsonObjectQueries.getCommonOrNull(type = type, url = visibility.urlOrigin, id = ref)
                            ?: jsonObjectQueries.getCommonGlobalOrNull(type = type, url = url, id = ref)
                    }
                    if (entity != null) {
                        currentEntry = entity.jsonObject
                        add(currentEntry)
                    }
                } while (idRef != null && entity != null)
            }
        }
    }

    suspend fun insert(
        entity: HookEntity
    ) = coroutineScopes.io.withContext {
        databaseTransactionFactory.transaction {
            hookQueries.insert(entity)
        }
    }

    suspend fun insert(
        entity: JsonObjectEntity,
        table: Table
    ) = coroutineScopes.io.withContext {
        databaseTransactionFactory.transactionWithResult {
            jsonObjectQueries.insert(entity, table)
        }
    }
}
