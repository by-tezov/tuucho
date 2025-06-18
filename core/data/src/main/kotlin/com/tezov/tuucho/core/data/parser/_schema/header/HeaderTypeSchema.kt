package com.tezov.tuucho.core.data.parser._schema.header

import com.tezov.tuucho.core.domain.model._system.string
import com.tezov.tuucho.core.domain.model._system.stringOrNull
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive

interface HeaderTypeSchema {

    object Name {
        const val type = "type"
    }

    companion object {

        val Map<String, JsonElement>.type get() = this[Name.type].string
        val Map<String, JsonElement>.typeOrNull get() = this[Name.type].stringOrNull

        fun MutableMap<String, JsonElement>.typePut(value: String) {
            put(Name.type, JsonPrimitive(value))
        }
    }
}



