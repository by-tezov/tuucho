package com.tezov.tuucho.core.data.database

import com.tezov.tuucho.core.data.database.dao.JsonObjectQueries
import com.tezov.tuucho.core.data.database.dao.VersioningQueries
import com.tezov.tuucho.core.data.database.entity.JsonObjectEntity
import com.tezov.tuucho.core.data.database.entity.VersioningEntity
import com.tezov.tuucho.core.data.database.type.Lifetime
import com.tezov.tuucho.core.domain.model.schema._system.onScope
import com.tezov.tuucho.core.domain.model.schema.material.IdSchema
import kotlinx.serialization.json.JsonObject

class MaterialDatabaseSource(
    private val versioningQueries: VersioningQueries,
    private val jsonObjectQueries: JsonObjectQueries,
) {

    @Suppress("RedundantSuspendModifier")
    suspend fun findRootOrNull(url: String): JsonObjectEntity? {
        val versioning = versioningQueries.get(url = url) ?: return null
        versioning.rootPrimaryKey ?: return null
        return jsonObjectQueries.get(versioning.rootPrimaryKey)

    }

    @Suppress("RedundantSuspendModifier")
    suspend fun findAllRefOrNull(
        from: JsonObject,
        url: String,
        urlOrigin: String?,
        type: String,
    ): List<JsonObject>? {
        from.onScope(IdSchema::Scope).source ?: return null
        var currentEntry = from
        val entries = mutableListOf(currentEntry)
        do {
            val idRef = currentEntry.onScope(IdSchema::Scope).source
            val entity = idRef?.let { ref ->
                urlOrigin?.let {
                    jsonObjectQueries.findShared(type = type, id = ref, urlOrigin = urlOrigin)
                } ?: run {
                    jsonObjectQueries.get(type = type, url = url, id = ref)
                        ?: jsonObjectQueries.findShared(type = type, id = ref, urlOrigin = null)
                }
            }
            if (entity != null) {
                currentEntry = entity.jsonObject
                entries.add(currentEntry)
            }
        } while (idRef != null && entity != null)
        return entries
    }

    @Suppress("RedundantSuspendModifier")
    suspend fun insertOrUpdate(entity: VersioningEntity) = versioningQueries.insertOrUpdate(entity)

    @Suppress("RedundantSuspendModifier")
    suspend fun insertOrUpdate(
        entity: JsonObjectEntity,
        lifetime: Lifetime,
    ) = jsonObjectQueries.insert(entity, lifetime)

    @Suppress("RedundantSuspendModifier")
    suspend fun clearTransient(lifetime: Lifetime) {
        val urls = versioningQueries.clearTransient(lifetime)
        jsonObjectQueries.deleteTransient(urls)
    }

}