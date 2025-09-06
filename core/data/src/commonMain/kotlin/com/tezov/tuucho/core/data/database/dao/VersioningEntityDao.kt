package com.tezov.tuucho.core.data.database.dao

import com.tezov.tuucho.core.data.database.Database
import com.tezov.tuucho.core.data.database.entity.VersioningEntity
import com.tezov.tuucho.core.data.database.entity.toEntity
import com.tezov.tuucho.core.data.database.type.Lifetime

class VersioningQueries(private val database: Database) {

    private val queries get() = database.versioningStatementQueries

    fun deleteAll() = queries.deleteAll()

    fun deleteAllTransient(lifetime: Lifetime): List<String> = database.transactionWithResult {
        val entities = queries
            .selectByLifetime("${lifetime.serialize()}%")
            .executeAsList().map { it.toEntity() }
        entities.map {
            queries.deleteByPrimaryKey(it.primaryKey!!)
            it.url
        }
    }

    fun delete(url: String) {
        queries.deleteByUrl(url)
    }

    fun insertOrUpdate(entity: VersioningEntity) {
        database.versioningStatementQueries.insert(
            url = entity.url,
            validityKey = entity.validityKey,
            validityDateTime = entity.validityDateTime?.toString(),
            validityTimeZone = entity.validityTimeZone?.toString(),
            rootPrimaryKey = entity.rootPrimaryKey,
            visibility = entity.visibility,
            lifetime = entity.lifetime,
        )
    }

    fun getValidityKey(url: String): String? = queries.getValidityKeyByUrl(url)
        .executeAsOneOrNull()?.validityKey

    fun get(url: String) = queries.getByUrl(url).executeAsOneOrNull()?.toEntity()
}


