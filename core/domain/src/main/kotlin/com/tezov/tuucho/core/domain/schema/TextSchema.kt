package com.tezov.tuucho.core.domain.schema

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive

interface TextSchema :
    com.tezov.tuucho.core.domain.schema.common.TypeSchema,
    com.tezov.tuucho.core.domain.schema.common.IdSchema {

    object Key {
        const val default = "default"
    }

    object Value {
        object Group {
            const val common = "common"
        }
    }

    companion object {
        fun MutableMap<String, JsonElement>.defaultPut(value: String) {
            put(Key.default, JsonPrimitive(value))
        }
    }
}



