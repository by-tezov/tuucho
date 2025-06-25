package com.tezov.tuucho.core.domain._system

import com.tezov.tuucho.core.domain.schema.common.IdSchema.Companion.idExist
import com.tezov.tuucho.core.domain.schema.common.SubsetSchema
import com.tezov.tuucho.core.domain.schema.common.TypeSchema
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

val JsonElement?.stringOrNull get() = this?.jsonPrimitive?.contentOrNull
val JsonElement?.string get() = this!!.jsonPrimitive.content

val JsonElement?.booleanOrNull: Boolean? get() = this?.jsonPrimitive?.boolean
val JsonElement?.boolean get() = this!!.jsonPrimitive.boolean

val JsonElement.isRef
    get():Boolean {
        val jsonObject = this as? JsonObject ?: return false
        if (!idExist) return false
        return (jsonObject
                - TypeSchema.Key.type
                - SubsetSchema.Key.subset
                ).size == 1
    }

fun JsonElement.findOrNull(path: JsonElementPath): JsonElement? {
    var currentElement: JsonElement = this
    path.forEach { key ->
        currentElement = (currentElement as? JsonObject)?.get(key) ?: return null
    }
    return currentElement
}

fun JsonElement.find(path: JsonElementPath): JsonElement =
    this.findOrNull(path) ?: throw NullPointerException("element could not be found at path $path inside $this")

fun JsonElement.replace(path: JsonElementPath, newElement: JsonElement): JsonElement {
    if (path.isEmpty()) return newElement
    val linkedElement = LinkedHashMap<String, JsonObject>()
    var currentElement: JsonElement = this
    for (key in path) {
        require(currentElement is JsonObject) { "invalid path: $key is not an object" }
        linkedElement[key] = currentElement
        currentElement = currentElement[key]
            ?: throw IllegalArgumentException("path not found: $path")
    }

    var updatedElement = newElement
    for ((key, element) in linkedElement.entries.reversed()) {
        updatedElement = element
            .toMutableMap()
            .apply { this[key] = updatedElement }
            .let(::JsonObject)
    }
    return updatedElement
}

