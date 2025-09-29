package com.tezov.tuucho.core.data.repository.database.entity

import com.tezov.tuucho.core.data.repository.database.table.JsonObjectCommonEntry
import com.tezov.tuucho.core.data.repository.database.table.JsonObjectContextualEntry
import kotlinx.serialization.json.JsonObject

data class JsonObjectEntity(
    val primaryKey: Long? = null,
    val type: String,
    val url: String,
    val id: String,
    val idFrom: String?,
    val jsonObject: JsonObject,
) {
    enum class Table {
        Common,
        Contextual
    }
}

fun JsonObjectCommonEntry.toEntity() = JsonObjectEntity(
    primaryKey = primaryKey,
    type = type,
    url = url,
    id = id,
    idFrom = idFrom,
    jsonObject = jsonObject
)

fun JsonObjectContextualEntry.toEntity() = JsonObjectEntity(
    primaryKey = primaryKey,
    type = type,
    url = url,
    id = id,
    idFrom = idFrom,
    jsonObject = jsonObject
)
