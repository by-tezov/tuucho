package com.tezov.tuucho.core.data.parser._system

import com.tezov.tuucho.core.data.parser._schema._common.header.HeaderIdSchema.Companion.idExist
import com.tezov.tuucho.core.data.parser._schema._common.header.HeaderSubsetSchema
import com.tezov.tuucho.core.data.parser._schema._common.header.HeaderTypeSchema
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

val JsonElement.isRef
    get():Boolean {
        val jsonObject = this as? JsonObject ?: return false
        if (!idExist) return false
        return (jsonObject
                - HeaderTypeSchema.Name.type
                - HeaderSubsetSchema.Name.subset
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
    this.findOrNull(path) ?: throw NullPointerException("element could not be found $path")

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