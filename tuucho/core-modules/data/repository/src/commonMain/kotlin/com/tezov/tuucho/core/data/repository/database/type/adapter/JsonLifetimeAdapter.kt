package com.tezov.tuucho.core.data.repository.database.type.adapter

import app.cash.sqldelight.ColumnAdapter
import com.tezov.tuucho.core.data.repository.database.type.JsonLifetime
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

internal class JsonLifetimeAdapter(
    private val json: Json,
) : ColumnAdapter<JsonLifetime, String> {
    companion object {
        private const val separator = ":"
    }

    private val serializer: KSerializer<JsonLifetime> = JsonLifetime.serializer()

    override fun decode(
        databaseValue: String
    ): JsonLifetime = json.decodeFromString(serializer, databaseValue.substringAfter(separator))

    override fun encode(
        value: JsonLifetime
    ): String = "${value.name}${separator}${json.encodeToString(serializer, value)}"
}
