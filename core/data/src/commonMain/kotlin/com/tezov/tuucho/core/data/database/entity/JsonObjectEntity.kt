package com.tezov.tuucho.core.data.database.entity

import com.tezov.tuucho.core.data.database.table.JsonObjectEntry
import kotlinx.serialization.json.JsonObject

data class JsonObjectEntity(
    val primaryKey: Long? = null,
    val type: String,
    val url: String,
    val id: String,
    val idFrom: String?,
    val jsonObject: JsonObject,
)

fun JsonObjectEntry.toEntity() = JsonObjectEntity(
    primaryKey = primaryKey,
    type = type,
    url = url,
    id = id,
    idFrom = idFrom,
    jsonObject = jsonObject
)