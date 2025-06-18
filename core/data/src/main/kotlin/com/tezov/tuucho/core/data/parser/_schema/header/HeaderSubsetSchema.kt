package com.tezov.tuucho.core.data.parser._schema.header

import com.tezov.tuucho.core.data.parser._system.JsonElementPath
import com.tezov.tuucho.core.data.parser._system.find
import com.tezov.tuucho.core.data.parser._system.findOrNull
import com.tezov.tuucho.core.data.parser._system.isRef
import com.tezov.tuucho.core.domain.model._system.string
import com.tezov.tuucho.core.domain.model._system.stringOrNull
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

interface HeaderSubsetSchema {

    object Name {
        const val subset = "subset"
    }

    object Default {
        const val subset = "unknown"
    }

    companion object {
        val Map<String, JsonElement>.subset get() = this[Name.subset].string
        val Map<String, JsonElement>.subsetOrNull get() = this[Name.subset].stringOrNull

        fun MutableMap<String, JsonElement>.subsetPut(value: String) {
            put(Name.subset, JsonPrimitive(value))
        }

        fun MutableMap<String, JsonElement>.subsetForwardOrMarkUnknownMaybe(
            path: JsonElementPath,
            element: JsonElement
        ) {
            (element.findOrNull(path.parent()) as? JsonObject)?.get(Name.subset)?.let {
                this[Name.subset] = it
            } ?:run {
                if((element.find(path) as? JsonObject)?.isRef != true) {
                    this[Name.subset] = JsonPrimitive(Default.subset)
                }
            }
        }
    }
}



