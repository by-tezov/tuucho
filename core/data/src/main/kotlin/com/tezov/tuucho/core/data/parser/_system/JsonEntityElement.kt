package com.tezov.tuucho.core.data.parser._system

import com.tezov.tuucho.core.data.cache.entity.JsonEntity
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement

sealed class JsonEntityElement

class JsonEntityObject(val content: JsonEntity) : JsonEntityElement() {

    var children: List<JsonEntityElement>? = null

    override fun equals(other: Any?) = content == other
    override fun hashCode() = content.hashCode()
    override fun toString() = content.toString()
}

fun JsonEntity.toJsonEntityObject() = JsonEntityObject(this)

class JsonEntityArray(private val content: List<JsonEntityElement>) : JsonEntityElement(),
    List<JsonEntityElement> by content {
    override fun equals(other: Any?) = content == other
    override fun hashCode() = content.hashCode()
    override fun toString() = content
        .joinToString(prefix = "[", postfix = "]", separator = ",")
}

fun List<JsonEntityElement>.toJsonEntityArray() = JsonEntityArray(this)

val JsonEntityElement.jsonEntityObject
    get() = this as? JsonEntityObject ?: error("JsonEntityPrimitive")

val JsonEntityElement.jsonEntityArray
    get() = this as? JsonEntityArray ?: error("JsonEntityList")

fun JsonEntityElement.flatten(): List<JsonEntityObject> {
    val output = mutableListOf<JsonEntityObject>()
    val stack = ArrayDeque<JsonEntityElement>()
    stack.add(this)
    while (stack.isNotEmpty()) {
        when (val current = stack.removeLast()) {
            is JsonEntityObject -> {
                output.add(current)
                val children = current.children
                if (children?.isNotEmpty() == true) {
                    current.children = null
                    stack.addAll(children.asReversed())
                }
            }

            is JsonEntityArray -> stack.addAll(current.asReversed())
        }
    }
    return output
}

fun JsonEntityElement.toJsonElement(): JsonElement = when (this) {
    is JsonEntityObject -> this.content.jsonObject
    is JsonEntityArray -> JsonArray(this.map { it.toJsonElement() })
}
