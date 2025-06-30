package com.tezov.tuucho.core.domain.schema

import com.tezov.tuucho.core.domain._system.string
import com.tezov.tuucho.core.domain._system.stringOrNull
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive

object DimensionSchema :
    TypeSchema,
    IdSchema {

    object Key {
        const val default = "default"
    }

    object Value {
        object Group {
            const val common = "common"
            const val font = "font"
            const val padding = "padding"
        }
    }

    val Map<String, JsonElement>.default get() = this[Key.default].string
    val Map<String, JsonElement>.defaultOrNull get() = this[Key.default].stringOrNull

    fun MutableMap<String, JsonElement>.defaultPut(value: String) {
        put(Key.default, JsonPrimitive(value))
    }

}



