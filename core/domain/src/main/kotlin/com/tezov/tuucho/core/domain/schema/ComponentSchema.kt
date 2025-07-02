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
        val JsonElement.contentObject get() = this.jsonObject[Key.content]!!.jsonObject
        val JsonElement.contentObjectOrNull get() = this.jsonObject[Key.content] as? JsonObject

        val JsonElement.styleObject get() = this.jsonObject[Key.style]!!.jsonObject
        val JsonElement.styleObjectOrNull get() = this.jsonObject[Key.style] as? JsonObject
    }
}
