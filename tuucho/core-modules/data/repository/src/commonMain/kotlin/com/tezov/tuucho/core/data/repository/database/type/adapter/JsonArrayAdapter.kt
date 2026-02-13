package com.tezov.tuucho.core.data.repository.database.type.adapter

import app.cash.sqldelight.ColumnAdapter
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray

internal class JsonArrayAdapter(
    private val json: Json
) : ColumnAdapter<JsonArray, String> {
    private val serializer = JsonArray.serializer()

    override fun decode(
        databaseValue: String
    ): JsonArray = json.decodeFromString(serializer, databaseValue)

    override fun encode(
        value: JsonArray
    ): String = json.encodeToString(serializer, value)
}
