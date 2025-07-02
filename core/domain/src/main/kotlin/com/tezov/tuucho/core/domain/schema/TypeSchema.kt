package com.tezov.tuucho.core.domain.schema

import com.tezov.tuucho.core.domain._system.string
import com.tezov.tuucho.core.domain._system.stringOrNull
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject

interface TypeSchema {

    object Key {
        const val type = "type"
    }

    object Value {
        object Type {
            const val component = "component"
            const val content = "content"
            const val style = "style"
            const val text = "text"
            const val dimension = "dimension"
            const val color = "color"
        }
    }

    companion object {

        val JsonElement.type get() = this.jsonObject[Key.type].string
        val JsonElement.typeOrNull get() = (this as? JsonObject)?.get(Key.type).stringOrNull

        fun MutableMap<String, JsonElement>.typePut(value: String) {
            put(Key.type, JsonPrimitive(value))
        }
    }
}



