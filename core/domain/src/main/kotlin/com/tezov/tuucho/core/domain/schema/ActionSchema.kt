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

        val JsonElement.actionObject get() = this.jsonObject[Content.Key.action]!!.jsonObject
        val JsonElement.actionObjectOrNull get() = this.jsonObject[Content.Key.action] as? JsonObject

        val JsonElement.value get() = this.jsonObject[Key.Action.value].string
        val JsonElement.valueOrNull get() = this.jsonObject[Key.Action.value].stringOrNull

        val JsonElement.params get() = this.jsonObject[Key.Action.params]!!.jsonObject
        val JsonElement.paramsOrNull get() = this.jsonObject[Key.Action.params] as? JsonObject

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



