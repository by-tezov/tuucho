package com.tezov.tuucho.core.data.repository.database.type.adapter

import app.cash.sqldelight.ColumnAdapter
import com.tezov.tuucho.core.data.repository.database.type.JsonVisibility
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

internal class JsonVisibilityAdapter(
    private val json: Json,
) : ColumnAdapter<JsonVisibility, String> {
    companion object {
        private const val separator = ":"
    }

    private val serializer: KSerializer<JsonVisibility> = JsonVisibility.serializer()

    override fun decode(
        databaseValue: String
    ): JsonVisibility = json.decodeFromString(serializer, databaseValue.substringAfter(separator))

    override fun encode(
        value: JsonVisibility
    ): String = "${value.name}${separator}${json.encodeToString(serializer, value)}"
}
