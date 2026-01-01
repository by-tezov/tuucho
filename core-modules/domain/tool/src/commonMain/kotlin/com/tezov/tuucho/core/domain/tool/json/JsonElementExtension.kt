package com.tezov.tuucho.core.domain.tool.json

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

val JsonElement?.stringOrNull get() = (this as? JsonPrimitive)?.contentOrNull
val JsonElement?.string
    get(): String {
        require(this != null) { "element is null" }
        return jsonPrimitive.content
    }

val JsonElement?.booleanOrNull: Boolean? get() = (this as? JsonPrimitive)?.boolean
val JsonElement?.boolean
    get(): Boolean {
        require(this != null) { "element is null" }
        return jsonPrimitive.boolean
    }

fun JsonElement.findOrNull(
    path: JsonElementPath
): JsonElement? { // IMPROVE: Not efficient, but will do the job for now
    var currentElement: JsonElement = this
    path.forEach { segment ->
        currentElement = if (segment.startsWith(JsonElementPath.INDEX_SEPARATOR)) {
            val index = segment.drop(1).toIntOrNull() ?: return null
            (currentElement as? JsonArray)?.getOrNull(index) ?: return null
        } else {
            (currentElement as? JsonObject)?.get(segment) ?: return null
        }
    }
    return currentElement
}

fun JsonElement.find(
    path: JsonElementPath
): JsonElement {
    val element = findOrNull(path)
    require(element != null) { "element could not be found at path $path inside $this" }
    return element
}

fun JsonElement.replaceOrInsert(
    path: JsonElementPath,
    newElement: JsonElement,
): JsonElement { // IMPROVE: No efficient, but will do the job
    if (path.contains(JsonElementPath.INDEX_SEPARATOR)) throw IllegalArgumentException("doesn't not work with array -> $path")

    if (path.isEmpty()) return newElement
    val stack = buildList {
        var current = this@replaceOrInsert
        for (segment in path) {
            require(current is JsonObject) { "invalid path: $segment is not an object" }
            add(segment to current)
            current = current[segment]
                ?: emptyMap<String, JsonElement>().let(::JsonObject)
        }
    }
    var updated = newElement
    for ((segment, parent) in stack.asReversed()) {
        updated = parent
            .toMutableMap()
            .apply { this[segment] = updated }
            .let(::JsonObject)
    }
    return updated
}
