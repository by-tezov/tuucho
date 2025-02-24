package com.tezov.tuucho.core.data.parser._schema

import com.tezov.tuucho.core.data.parser._schema._common.header.HeaderIdSchemaData
import com.tezov.tuucho.core.data.parser._schema._common.header.HeaderTypeSchemaData
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive

interface TextSchemaData :
    HeaderTypeSchemaData,
    HeaderIdSchemaData {

    object Name {
        const val default = "default"
    }

    object Default {
        const val type = "text"
        object Group {
            const val common = "common"
        }
    }

    companion object {
        fun MutableMap<String, JsonElement>.defaultPut(value: String) {
            put(Name.default, JsonPrimitive(value))
        }
    }
}



