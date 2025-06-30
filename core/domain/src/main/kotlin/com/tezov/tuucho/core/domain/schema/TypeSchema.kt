package com.tezov.tuucho.core.domain.schema

import com.tezov.tuucho.core.domain._system.string
import com.tezov.tuucho.core.domain._system.stringOrNull
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive

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

        val Map<String, JsonElement>.type get() = this[Key.type].string
        val Map<String, JsonElement>.typeOrNull get() = this[Key.type].stringOrNull

        fun MutableMap<String, JsonElement>.typePut(value: String) {
            put(Key.type, JsonPrimitive(value))
        }
    }
}



