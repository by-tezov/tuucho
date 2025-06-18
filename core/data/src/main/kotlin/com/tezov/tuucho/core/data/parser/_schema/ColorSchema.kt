package com.tezov.tuucho.core.data.parser._schema

import com.tezov.tuucho.core.data.parser._schema.header.HeaderIdSchema
import com.tezov.tuucho.core.data.parser._schema.header.HeaderTypeSchema
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive

interface ColorSchema :
    HeaderTypeSchema,
    HeaderIdSchema {

    object Name {
        const val default = "default"
    }

    object Default {
        const val type = "color"
        object Group {
            const val common = "common"
            const val palette = "palette"
        }
    }

    companion object {
        fun MutableMap<String, JsonElement>.defaultPut(value: String) {
            put(Name.default, JsonPrimitive(value))
        }
    }

}



