package com.tezov.tuucho.core.data.database.dao

import com.tezov.tuucho.core.data.database.Database
import com.tezov.tuucho.core.data.database.entity.VersioningEntity
import com.tezov.tuucho.core.data.database.entity.toEntity
import com.tezov.tuucho.core.data.database.type.Lifetime

class VersioningQueries(private val database: Database) {

    private val queries get() = database.versioningStatementQueries

    fun deleteAll() = queries.deleteAll()

    fun clearTransient(lifetime: Lifetime): List<String> = database.transactionWithResult {
        val entities = queries
            .selectByLifetime("${lifetime.to()}%")
            .executeAsList().map { it.toEntity() }
        entities.map {
            queries.deleteByPrimaryKey(it.primaryKey!!)
            it.url
        }
    }

    fun insertOrUpdate(entity: VersioningEntity) {
        database.transaction {
            database.versioningStatementQueries.insert(
                url = entity.url,
                version = entity.version,
                rootPrimaryKey = entity.rootPrimaryKey,
                visibility = entity.visibility,
                lifetime = entity.lifetime,
            )
        }
    }

    fun getVersion(url: String): String {
        return queries.getVersionByUrl(url).executeAsOne()
    }

    fun get(url: String): VersioningEntity? {
        return queries.getByUrl(url).executeAsOneOrNull()?.toEntity()
    }
}


