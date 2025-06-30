package com.tezov.tuucho.core.domain.schema

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject

interface ComponentSchema :
    TypeSchema,
    IdSchema,
    SubsetSchema {

    object Key {
        const val content = "content"
        const val style = "style"
    }

    object Value

    companion object {
        val Map<String, JsonElement>.contentObject get() = this[Key.content]!!.jsonObject
        val Map<String, JsonElement>.contentObjectOrNull get() = this[Key.content] as? JsonObject

        val Map<String, JsonElement>.styleObject get() = this[Key.style]!!.jsonObject
        val Map<String, JsonElement>.styleObjectOrNull get() = this[Key.style] as? JsonObject
    }
}
