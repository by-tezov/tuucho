package com.tezov.tuucho.core.data.database.dao

import com.tezov.tuucho.core.data.database.Database
import com.tezov.tuucho.core.data.database.entity.VersioningEntity
import com.tezov.tuucho.core.data.database.entity.toEntity

fun Database.versioning() = VersioningQueries(this)

@JvmInline
value class VersioningQueries(private val database: Database) {
    private val queries get() = database.versioningStatementQueries

    fun selectAll(): List<VersioningEntity> {
        return queries.selectAll().executeAsList().map { it.toEntity() }
    }

    fun insertOrUpdate(entity: VersioningEntity) {
        database.transaction {
            if(queries.existWithUrl(entity.url).executeAsOne()) {
                queries.updateByUrl(
                    url = entity.url,
                    version = entity.version,
                    rootPrimaryKey = entity.rootPrimaryKey,
                    isShared = entity.isShared
                )
            }
            else {
                database.versioningStatementQueries.insert(
                    url = entity.url,
                    version = entity.version,
                    rootPrimaryKey = entity.rootPrimaryKey,
                    isShared = entity.isShared
                )
            }
        }
    }

    fun getVersion(url: String): String {
        return queries.version(url).executeAsOne()
    }

    fun find(url: String): VersioningEntity? {
        return queries.find(url).executeAsOneOrNull()?.toEntity()
    }
}


