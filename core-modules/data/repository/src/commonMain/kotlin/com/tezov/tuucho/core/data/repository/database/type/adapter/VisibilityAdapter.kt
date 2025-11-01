package com.tezov.tuucho.core.data.repository.database.type.adapter

import app.cash.sqldelight.ColumnAdapter
import com.tezov.tuucho.core.data.repository.database.type.Visibility
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

internal class VisibilityAdapter(
    private val json: Json,
) : ColumnAdapter<Visibility, String> {

    companion object {
        private const val separator = ":"
    }

    private val serializer: KSerializer<Visibility> = Visibility.serializer()

    override fun decode(databaseValue: String): Visibility {
        return json.decodeFromString(serializer, databaseValue.substringAfter(separator))
    }

    override fun encode(value: Visibility): String {
        return "${value.name}${separator}${json.encodeToString(serializer, value)}"
    }
}