package com.tezov.tuucho.core.data.cache.converter

import androidx.room.TypeConverter
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

class JsonObjectConverter {

    private val json = Json
    private val serializer = JsonObject.serializer()

    @TypeConverter
    fun fromMap(jsonObject: JsonObject): String {
        return json.encodeToString(serializer, jsonObject)
    }

    @TypeConverter
    fun toMap(value: String): JsonObject {
        return json.decodeFromString(serializer, value)
    }
}