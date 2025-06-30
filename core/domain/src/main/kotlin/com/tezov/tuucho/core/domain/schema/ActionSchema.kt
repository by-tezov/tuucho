package com.tezov.tuucho.core.domain.schema

import com.tezov.tuucho.core.domain._system.string
import com.tezov.tuucho.core.domain._system.stringOrNull
import com.tezov.tuucho.core.domain.schema._element.ButtonSchema.Content
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject

interface ActionSchema {

    object Key {
        const val action = "action"

        object Action {
            const val value = "value"
            const val params = "params"
        }
    }

    object Value

    companion object {

        val Map<String, JsonElement>.actionObject get() = this[Content.Key.action]!!.jsonObject
        val Map<String, JsonElement>.actionObjectOrNull get() = this[Content.Key.action] as? JsonObject

        val Map<String, JsonElement>.value get() = this[Key.Action.value].string
        val Map<String, JsonElement>.valueOrNull get() = this[Key.Action.value].stringOrNull

        val Map<String, JsonElement>.params get() = this[Key.Action.params]!!.jsonObject
        val Map<String, JsonElement>.paramsOrNull get() = this[Key.Action.params] as? JsonObject

        fun MutableMap<String, JsonElement>.actionPutObject(
            value: String?,
            params: JsonObject?
        ) {
            put(Key.action, JsonObject(mutableMapOf<String, JsonObject>().apply {
                if (value != null) {
                    put(Key.Action.value, JsonPrimitive(value))

                } else {
                    remove(Key.Action.value)
                }
                if (params != null) {
                    put(Key.Action.params, params)
                } else {
                    remove(Key.Action.params)
                }
            }))
        }
    }



}



