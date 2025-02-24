package com.tezov.tuucho.core.data.parser._schema

import com.tezov.tuucho.core.data.parser._schema._common.header.HeaderIdSchemaData
import com.tezov.tuucho.core.data.parser._schema._common.header.HeaderTypeSchemaData
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive

interface DimensionSchemaData :
    HeaderTypeSchemaData,
    HeaderIdSchemaData {

    object Name {
        const val default = "default"
    }

    object Default {
        const val type = "dimension"
        object Group {
            const val common = "common"
            const val font = "font"
            const val padding = "padding"
        }
    }

    companion object {
        fun MutableMap<String, JsonElement>.defaultPut(value: String) {
            put(Name.default, JsonPrimitive(value))
        }
    }

}



