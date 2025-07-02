package com.tezov.tuucho.core.domain.schema

import com.tezov.tuucho.core.domain._system.string
import com.tezov.tuucho.core.domain._system.stringOrNull
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject

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

    val JsonElement.default get() = this.jsonObject[Key.default].string
    val JsonElement.defaultOrNull get() = this.jsonObject[Key.default].stringOrNull

    fun MutableMap<String, JsonElement>.defaultPut(value: String) {
        put(Key.default, JsonPrimitive(value))
    }

}



