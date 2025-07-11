package com.tezov.tuucho.core.domain.schema

import com.tezov.tuucho.core.domain._system.JsonElementPath
import com.tezov.tuucho.core.domain._system.find
import com.tezov.tuucho.core.domain._system.findOrNull
import com.tezov.tuucho.core.domain._system.isRef
import com.tezov.tuucho.core.domain._system.string
import com.tezov.tuucho.core.domain._system.stringOrNull
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject

interface SubsetSchema {

    object Key {
        const val subset = "subset"
    }

    object Value {
        object Subset {
            const val unknown = "unknown"
        }
    }

    companion object {
        val JsonElement.subset get() = this.jsonObject[Key.subset].string
        val JsonElement.subsetOrNull get() = (this as? JsonObject)?.get(Key.subset).stringOrNull

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
                    this[Key.subset] = JsonPrimitive(Value.Subset.unknown)
                }
            }
        }
    }
}



