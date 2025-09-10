package com.tezov.tuucho.core.data.database.type.adapter

import app.cash.sqldelight.ColumnAdapter
import com.tezov.tuucho.core.data.database.type.Lifetime
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

class LifetimeAdapter(
    private val json: Json,
) : ColumnAdapter<Lifetime, String> {

    companion object {
        private const val separator = ":"
    }

    private val serializer: KSerializer<Lifetime> = Lifetime.serializer()

    override fun decode(databaseValue: String): Lifetime {
        return json.decodeFromString(serializer, databaseValue.substringAfter(separator))
    }

    override fun encode(value: Lifetime): String {
        return "${value.name}${separator}${json.encodeToString(serializer, value)}"
    }

}