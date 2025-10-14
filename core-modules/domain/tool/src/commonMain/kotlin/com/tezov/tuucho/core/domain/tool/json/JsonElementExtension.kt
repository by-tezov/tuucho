package com.tezov.tuucho.core.domain.tool.json

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

val JsonElement?.stringOrNull get() = (this as? JsonPrimitive)?.contentOrNull
val JsonElement?.string
    get():String {
        require(this != null) { "element is null" }
        return jsonPrimitive.content
    }

val JsonElement?.booleanOrNull: Boolean? get() = (this as? JsonPrimitive)?.boolean
val JsonElement?.boolean
    get():Boolean {
        require(this != null) { "element is null" }
        return jsonPrimitive.boolean
    }

fun JsonElement.findOrNull(path: JsonElementPath): JsonElement? { //IMPROVE: Not efficient, but will do the job for now
    var currentElement: JsonElement = this
    path.forEach { key ->
        currentElement = (currentElement as? JsonObject)?.get(key) ?: return null
    }
    return currentElement
}

fun JsonElement.find(path: JsonElementPath): JsonElement {
    val element = findOrNull(path)
    require(element != null) { "element could not be found at path $path inside $this" }
    return element
}


fun JsonElement.replaceOrInsert(
    path: JsonElementPath,
    newElement: JsonElement,
): JsonElement { //IMPROVE: No efficient, but will do the job
    if (path.isEmpty()) return newElement
    val stack = buildList {
        var current = this@replaceOrInsert
        for (key in path) {
            require(current is JsonObject) { "invalid path: $key is not an object" }
            add(key to current)
            current = current[key]
                ?: emptyMap<String, JsonElement>().let(::JsonObject)
        }
    }
    var updated = newElement
    for ((key, parent) in stack.asReversed()) {
        updated = parent
            .toMutableMap()
            .apply { this[key] = updated }
            .let(::JsonObject)
    }
    return updated
}
