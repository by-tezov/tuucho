package com.tezov.tuucho.core.data.database

import com.tezov.tuucho.core.data.database.dao.JsonObjectQueries
import com.tezov.tuucho.core.data.database.dao.VersioningQueries
import com.tezov.tuucho.core.data.database.entity.JsonObjectEntity
import com.tezov.tuucho.core.data.database.entity.VersioningEntity
import com.tezov.tuucho.core.data.database.type.Lifetime
import com.tezov.tuucho.core.domain.business.jsonSchema._system.onScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.IdSchema
import kotlinx.serialization.json.JsonObject

class MaterialDatabaseSource(
    private val transactionFactory: DatabaseTransactionFactory,
    private val versioningQueries: VersioningQueries,
    private val jsonObjectQueries: JsonObjectQueries,
) {

    @Suppress("RedundantSuspendModifier")
    suspend fun deleteAll() {
        transactionFactory.transaction {
            versioningQueries.deleteAll()
            jsonObjectQueries.deleteAll()
        }
    }

    @Suppress("RedundantSuspendModifier")
    suspend fun deleteAll(url: String) {
        transactionFactory.transaction {
            versioningQueries.delete(url)
            jsonObjectQueries.deleteAll(url)

            //TODO: contextual ?

        }
    }

//    @Suppress("RedundantSuspendModifier")
//    suspend fun deleteAllTransient(lifetime: Lifetime) {
//        transactionFactory.transaction {
//            val urls = versioningQueries.deleteAllTransient(lifetime)
//            jsonObjectQueries.deleteTransient(urls)
//        }
//    }

    @Suppress("RedundantSuspendModifier")
    suspend fun getVersioningEntityOrNull(url: String) = versioningQueries.getOrNull(url = url)

    @Suppress("RedundantSuspendModifier")
    suspend fun getRootJsonObjectEntityOrNull(primaryKey: Long) = jsonObjectQueries.getOrNull(primaryKey)

    @Suppress("RedundantSuspendModifier")
    suspend fun getRootJsonObjectEntityOrNull(url: String): JsonObjectEntity? {
        val versioning = versioningQueries.getOrNull(url = url) ?: return null
        versioning.rootPrimaryKey ?: return null
        return jsonObjectQueries.getOrNull(versioning.rootPrimaryKey)
    }

    @Suppress("RedundantSuspendModifier")
    suspend fun getLifetimeOrNull(url: String): Lifetime? =
        versioningQueries.getLifetimeOrNull(url)

    @Suppress("RedundantSuspendModifier")
    suspend fun getAllRefOrNull(
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
                    jsonObjectQueries.getOrNull(type = type, url = url, id = ref)
                        ?: jsonObjectQueries.getGlobalOrNull(type = type, id = ref)
                }
                if (entity != null) {
                    currentEntry = entity.jsonObject
                    add(currentEntry)
                }
            } while (idRef != null && entity != null)
        }
    }

//    @Suppress("RedundantSuspendModifier")
//    suspend fun findAllRefOrNull2(
//        from: JsonObject,
//        url: String,
//        urlOrigin: String?,
//        type: String,
//    ): List<JsonObject>? {
//        from.onScope(IdSchema::Scope).source ?: return null
//        return buildList {
//            var currentEntry = from
//            add(currentEntry)
//            do {
//                val idRef = currentEntry.onScope(IdSchema::Scope).source
//                val entity = idRef?.let { ref ->
//                    urlOrigin?.let {
//                        jsonObjectQueries.findShared(type = type, id = ref, urlOrigin = urlOrigin)
//                    } ?: run {
//                        jsonObjectQueries.getOrNull(type = type, url = url, id = ref)
//                            ?: jsonObjectQueries.findShared(type = type, id = ref, urlOrigin = null)
//                    }
//                }
//                if (entity != null) {
//                    currentEntry = entity.jsonObject
//                    add(currentEntry)
//                }
//            } while (idRef != null && entity != null)
//        }
//    }

    @Suppress("RedundantSuspendModifier")
    suspend fun insertOrUpdate(entity: VersioningEntity) = versioningQueries.insertOrUpdate(entity)

    @Suppress("RedundantSuspendModifier")
    suspend fun insert(
        entity: JsonObjectEntity,
    ) = transactionFactory.transactionWithResult {
        jsonObjectQueries.insert(entity)
    }

    @Suppress("RedundantSuspendModifier")
    suspend fun insertContextual(
        entity: JsonObjectEntity,
    ) = transactionFactory.transactionWithResult {
        jsonObjectQueries.insertContextual(entity)
    }


}