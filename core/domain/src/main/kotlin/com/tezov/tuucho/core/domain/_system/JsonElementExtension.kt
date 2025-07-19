package com.tezov.tuucho.core.domain._system

import com.tezov.tuucho.core.domain.model.schema._system.OpenSchemaScope
import com.tezov.tuucho.core.domain.model.schema.material.IdSchema
import com.tezov.tuucho.core.domain.model.schema.material.SubsetSchema
import com.tezov.tuucho.core.domain.model.schema.material.TypeSchema
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

val JsonElement?.stringOrNull get() = (this as? JsonPrimitive)?.contentOrNull
val JsonElement?.string get() = this!!.jsonPrimitive.content

val JsonElement?.booleanOrNull: Boolean? get() = (this as? JsonPrimitive)?.boolean
val JsonElement?.boolean get() = this!!.jsonPrimitive.boolean

val OpenSchemaScope<*>.isRef: Boolean
    get() {
        withScope(IdSchema::Scope).self ?: return false
        return element.jsonObject.size == (1
                + (withScope(TypeSchema::Scope).self?.let { 1 } ?: 0)
                + (withScope(SubsetSchema::Scope).self?.let { 1 } ?: 0))
    }

fun JsonElement.findOrNull(path: JsonElementPath): JsonElement? {
    var currentElement: JsonElement = this
    path.forEach { key ->
        currentElement = (currentElement as? JsonObject)?.get(key) ?: return null
    }
    return currentElement
}

fun JsonElement.find(path: JsonElementPath): JsonElement =
    this.findOrNull(path)
        ?: throw NullPointerException("element could not be found at path $path inside $this")

fun JsonElement.replace(path: JsonElementPath, newElement: JsonElement): JsonElement {
    if (path.isEmpty()) return newElement
    val stack = mutableListOf<Pair<String, JsonObject>>()
    var current = this
    for (key in path) {
        require(current is JsonObject) { "invalid path: $key is not an object" }
        stack.add(key to current)
        current = current[key]
            ?: throw IllegalArgumentException("path not found: $path")
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
