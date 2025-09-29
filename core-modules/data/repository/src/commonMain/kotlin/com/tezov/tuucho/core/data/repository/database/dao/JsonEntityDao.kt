package com.tezov.tuucho.core.data.repository.database.dao

import com.tezov.tuucho.core.data.repository.database.Database
import com.tezov.tuucho.core.data.repository.database.entity.JsonObjectEntity
import com.tezov.tuucho.core.data.repository.database.entity.JsonObjectEntity.Table
import com.tezov.tuucho.core.data.repository.database.entity.toEntity
import com.tezov.tuucho.core.data.repository.database.type.Visibility

class JsonObjectQueries(
    private val database: Database,
) {

    private val queriesCommon get() = database.jsonObjectCommonStatementQueries

    private val queriesContextual get() = database.jsonObjectContextualStatementQueries

    private val queriesJoin get() = database.joinStatementQueries

    fun deleteAll(url: String, table: Table) {
        when (table) {
            Table.Common -> queriesCommon.deleteByUrl(url)
            Table.Contextual -> queriesContextual.deleteByUrl(url)
        }
    }

    fun insert(entity: JsonObjectEntity, table: Table) = when (table) {
        Table.Common -> {
            queriesCommon.insert(
                type = entity.type,
                url = entity.url,
                id = entity.id,
                idFrom = entity.idFrom,
                jsonObject = entity.jsonObject
            )
            queriesCommon.lastInsertedId().executeAsOne()
        }

        Table.Contextual -> {
            queriesContextual.insert(
                type = entity.type,
                url = entity.url,
                id = entity.id,
                idFrom = entity.idFrom,
                jsonObject = entity.jsonObject
            )
            queriesContextual.lastInsertedId().executeAsOne()
        }
    }

    fun getCommonOrNull(primaryKey: Long) =
        queriesCommon.getByPrimaryKey(primaryKey).executeAsOneOrNull()?.toEntity()

    fun getCommonOrNull(
        type: String,
        url: String,
        id: String,
    ) = queriesCommon.getByTypeUrlId(type, url, id).executeAsOneOrNull()?.toEntity()

    fun getCommonGlobalOrNull(type: String, id: String) = queriesJoin
        .getCommonByTypeIdVisibility(Visibility.Global, type, id)
        .executeAsOneOrNull()?.toEntity()

    fun getContextualOrNull(
        type: String,
        url: String,
        id: String,
        visibility: Visibility,
    ) = queriesJoin
        .getContextualByTypeUrlIdVisibility(visibility, type, url, id)
        .executeAsOneOrNull()?.toEntity()
}




