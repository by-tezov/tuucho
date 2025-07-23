package com.tezov.tuucho.core.data.database.dao

import com.tezov.tuucho.core.data.database.Database
import com.tezov.tuucho.core.data.database.entity.JsonObjectEntity
import com.tezov.tuucho.core.data.database.entity.toEntity

class JsonObjectQueries private constructor(private val database: Database) {

    companion object {
        fun Database.jsonObject() = JsonObjectQueries(this)
    }

    private val queries get() = database.jsonObjectStatementQueries

    fun selectAll(): List<JsonObjectEntity> =
        queries.selectAll().executeAsList().map { it.toEntity() }

    fun insertOrUpdate(entity: JsonObjectEntity) = entity.primaryKey?.also {
        queries.update(
            primaryKey = entity.primaryKey,
            type = entity.type,
            url = entity.url,
            id = entity.id,
            idFrom = entity.idFrom,
            jsonObject = entity.jsonObject
        )
    } ?: run {
        database.transactionWithResult {
            queries.insert(
                type = entity.type,
                url = entity.url,
                id = entity.id,
                idFrom = entity.idFrom,
                jsonObject = entity.jsonObject
            )
            queries.lastInsertedId()
        }.executeAsOne()
    }

    fun find(primaryKey: Long): JsonObjectEntity? =
        queries.findByPrimaryKey(primaryKey).executeAsOneOrNull()?.toEntity()

    fun find(type: String, url: String, id: String): JsonObjectEntity? =
        queries.findByTypeUrlId(type, url, id).executeAsOneOrNull()?.toEntity()

    fun findShared(type: String, id: String): JsonObjectEntity? =
        queries.findShared(type, id).executeAsOneOrNull()?.toEntity()
}




