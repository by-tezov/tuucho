package com.tezov.tuucho.core.data.database.dao

import com.tezov.tuucho.core.data.database.Database
import com.tezov.tuucho.core.data.database.entity.JsonObjectEntity
import com.tezov.tuucho.core.data.database.entity.toEntity

class JsonObjectQueries(private val database: Database) {

    private val queries get() = database.jsonObjectStatementQueries

    private val queriesContextual get() = database.jsonObjectContextualStatementQueries

    private val queriesJoin get() = database.joinStatementQueries

    fun deleteAll() {
        queries.deleteAll()
        queriesContextual.deleteAll()
    }

    fun deleteTransient(urls: List<String>) {
        urls.forEach {
            queriesContextual.deleteByUrl(it)
        }
    }

    fun deleteAll(url: String) {
        queries.deleteByUrl(url)
    }

    fun insert(entity: JsonObjectEntity): Long {
        queries.insert(
            type = entity.type,
            url = entity.url,
            id = entity.id,
            idFrom = entity.idFrom,
            jsonObject = entity.jsonObject
        )
        return queries.lastInsertedId().executeAsOne()
    }

    fun insertContextual(entity: JsonObjectEntity): Long {
        queriesContextual.insert(
            type = entity.type,
            url = entity.url,
            id = entity.id,
            idFrom = entity.idFrom,
            jsonObject = entity.jsonObject
        )
        return queriesContextual.lastInsertedId().executeAsOne()
    }

    fun getOrNull(primaryKey: Long): JsonObjectEntity? =
        queries.getByPrimaryKey(primaryKey).executeAsOneOrNull()?.toEntity()

    fun getOrNull(type: String, url: String, id: String): JsonObjectEntity? =
        queries.getByTypeUrlId(type, url, id).executeAsOneOrNull()?.toEntity()

    fun getGlobalOrNull(type: String, id: String) = queriesJoin
        .getGlobalByTypeId(type, id)
        .executeAsOneOrNull()?.toEntity()

//    fun findShared2(type: String, id: String, urlOrigin: String?): JsonObjectEntity? = queriesShared
//        .getGlobalUnlimitedByTypeId(type, id)
//        .executeAsOneOrNull()?.toEntity()
//        ?: urlOrigin?.let {
//            queriesShared
//                .getLocalTransientByTypeIdUrlOrigin(type, id, urlOrigin)
//                .executeAsOneOrNull()?.toEntity()
//        }

}




