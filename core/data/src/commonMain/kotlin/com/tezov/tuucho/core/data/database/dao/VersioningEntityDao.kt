package com.tezov.tuucho.core.data.database.dao

import com.tezov.tuucho.core.data.database.Database
import com.tezov.tuucho.core.data.database.entity.VersioningEntity
import com.tezov.tuucho.core.data.database.entity.toEntity
import com.tezov.tuucho.core.data.database.type.Lifetime

class VersioningQueries(private val database: Database) {

    private val queries get() = database.versioningStatementQueries

    fun deleteAll() = queries.deleteAll()

    fun deleteAllTransient(lifetime: Lifetime): List<String> = database.transactionWithResult {
//        val entities = queries
//            .selectByLifetime("${lifetime.serialize()}%")
//            .executeAsList().map { it.toEntity() }
//        entities.map {
//            queries.deleteByPrimaryKey(it.primaryKey!!)
//            it.url
//        }
        TODO()
    }

    fun delete(url: String) {
        queries.deleteByUrl(url)
    }

    fun insertOrUpdate(entity: VersioningEntity) {
        database.versioningStatementQueries.insert(
            url = entity.url,
            rootPrimaryKey = entity.rootPrimaryKey,
            visibility = entity.visibility,
            lifetime = entity.lifetime,
        )
    }

    fun getOrNull(url: String) =
        queries.getByUrl(url).executeAsOneOrNull()?.toEntity()

    fun getLifetimeOrNull(url: String) =
        queries.getLifetimeByUrl(url).executeAsOneOrNull()
}


