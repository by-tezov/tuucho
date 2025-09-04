package com.tezov.tuucho.core.data.database.dao

import com.tezov.tuucho.core.data.database.Database
import com.tezov.tuucho.core.data.database.entity.JsonObjectEntity
import com.tezov.tuucho.core.data.database.entity.toEntity
import com.tezov.tuucho.core.data.database.type.Lifetime

class JsonObjectQueries(private val database: Database) {

    private val queries get() = database.jsonObjectStatementQueries

    private val queriesTransient get() = database.jsonObjectTransientStatementQueries

    private val queriesShared get() = database.sharedStatementQueries

    fun deleteAll() {
        queries.deleteAll()
        queriesTransient.deleteAll()
    }

    fun deleteTransient(urls: List<String>) {
        urls.forEach {
            queriesTransient.deleteByUrl(it)
        }
    }

    fun deleteAll(url: String) {
        queries.deleteByUrl(url)
    }

    fun insert(entity: JsonObjectEntity, lifetime: Lifetime) = when (lifetime) {
        Lifetime.Unlimited -> {
            queries.insert(
                type = entity.type,
                url = entity.url,
                id = entity.id,
                idFrom = entity.idFrom,
                jsonObject = entity.jsonObject
            )
            queries.lastInsertedId().executeAsOne()
        }

        is Lifetime.Transient -> {
            queriesTransient.insert(
                type = entity.type,
                url = entity.url,
                urlOrigin = lifetime.urlOrigin,
                id = entity.id,
                idFrom = entity.idFrom,
                jsonObject = entity.jsonObject
            )
            queriesTransient.lastInsertedId().executeAsOne()
        }
    }

    fun get(primaryKey: Long): JsonObjectEntity? =
        queries.getByPrimaryKey(primaryKey).executeAsOneOrNull()?.toEntity()

    fun get(type: String, url: String, id: String): JsonObjectEntity? =
        queries.getByTypeUrlId(type, url, id).executeAsOneOrNull()?.toEntity()

    fun findShared(type: String, id: String, urlOrigin: String?) = queriesShared
        .getGlobalUnlimitedByTypeId(type, id)
        .executeAsOneOrNull()?.toEntity()
        ?: urlOrigin?.let {
            queriesShared
                .getLocalTransientByTypeIdUrlOrigin(type, id, urlOrigin)
                .executeAsOneOrNull()?.toEntity()
        }
}




