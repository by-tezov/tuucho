package com.tezov.tuucho.core.data.database.converter

import app.cash.sqldelight.ColumnAdapter
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

class JsonObjectConverter: ColumnAdapter<JsonObject, String> {

    private val json = Json
    private val serializer = JsonObject.serializer()

    override fun decode(databaseValue: String): JsonObject {
        return json.decodeFromString(serializer, databaseValue)
    }

    override fun encode(value: JsonObject): String {
        return json.encodeToString(serializer, value)
    }
}