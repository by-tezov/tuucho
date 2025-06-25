package com.tezov.tuucho.core.domain.schema.common

import com.tezov.tuucho.core.domain._system.JsonElementPath
import com.tezov.tuucho.core.domain._system.find
import com.tezov.tuucho.core.domain._system.findOrNull
import com.tezov.tuucho.core.domain._system.isRef
import com.tezov.tuucho.core.domain._system.string
import com.tezov.tuucho.core.domain._system.stringOrNull
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

interface SubsetSchema {

    object Key {
        const val subset = "subset"
    }

    object Value {
        const val subset = "unknown"
    }

    companion object {
        val Map<String, JsonElement>.subset get() = this[Key.subset].string
        val Map<String, JsonElement>.subsetOrNull get() = this[Key.subset].stringOrNull

        fun MutableMap<String, JsonElement>.subsetPut(value: String) {
            put(Key.subset, JsonPrimitive(value))
        }

        fun MutableMap<String, JsonElement>.subsetForwardOrMarkUnknownMaybe(
            path: JsonElementPath,
            element: JsonElement
        ) {
            (element.findOrNull(path.parent()) as? JsonObject)?.get(Key.subset)?.let {
                this[Key.subset] = it
            } ?:run {
                if((element.find(path) as? JsonObject)?.isRef != true) {
                    this[Key.subset] = JsonPrimitive(Value.subset)
                }
            }
        }
    }
}



