package com.tezov.tuucho.core.data.cache.converter

import androidx.room.TypeConverter
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

class JsonElementConverter {

    private val json = Json
    private val mapSerializer = JsonElement.serializer()

    @TypeConverter
    fun fromMap(map: JsonElement): String {
        return json.encodeToString(mapSerializer, map)
    }

    @TypeConverter
    fun toMap(value: String): JsonElement {
        return json.decodeFromString(mapSerializer, value)
    }
}