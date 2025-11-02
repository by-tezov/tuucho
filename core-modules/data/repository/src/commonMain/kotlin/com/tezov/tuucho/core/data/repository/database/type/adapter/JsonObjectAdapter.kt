package com.tezov.tuucho.core.data.repository.database.type.adapter

import app.cash.sqldelight.ColumnAdapter
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

internal class JsonObjectAdapter(
    private val json: Json
) : ColumnAdapter<JsonObject, String> {
    private val serializer = JsonObject.serializer()

    override fun decode(
        databaseValue: String
    ): JsonObject = json.decodeFromString(serializer, databaseValue)

    override fun encode(
        value: JsonObject
    ): String = json.encodeToString(serializer, value)
}
