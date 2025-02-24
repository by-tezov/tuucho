package com.tezov.tuucho.core.data.parser._system

import com.tezov.tuucho.core.data.cache.entity.JsonEntity
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull

sealed class JsonEntityElement

sealed class JsonEntityPrimitive: JsonEntityElement() {

    private class Constructor(
        override val content: JsonEntity
    ) : JsonEntityPrimitive()

    abstract val content: JsonEntity

    companion object {

        operator fun invoke(content: JsonEntity?): JsonEntityPrimitive {
            if (content == null) return JsonEntityNull
            return Constructor(content)
        }

        @Suppress("UNUSED_PARAMETER") // allows to call `JsonPrimitive(null)`
        operator fun invoke(value: Nothing?): JsonEntityPrimitive {
            return JsonEntityNull
        }
    }

    override fun toString(): String = content.toString()
}

object JsonEntityNull : JsonEntityPrimitive() {
    override val content: JsonEntity
        get() = throw NullPointerException("illegal access")
}

class JsonEntityList(private val content: List<JsonEntityElement>) : JsonEntityElement(),
    List<JsonEntityElement> by content {
    override fun equals(other: Any?): Boolean = content == other
    override fun hashCode(): Int = content.hashCode()
    override fun toString(): String =
        content.joinToString(prefix = "[", postfix = "]", separator = ",")
}

val JsonEntityElement.jsonEntityPrimitive: JsonEntityPrimitive
    get() = this as? JsonEntityPrimitive ?: error("JsonEntityPrimitive")

val JsonEntityElement.jsonEntityList: JsonEntityList
    get() = this as? JsonEntityList ?: error("JsonEntityList")

val JsonEntityElement.jsonEntityNull: JsonEntityNull
    get() = this as? JsonEntityNull ?: error("JsonEntityNull")

fun JsonEntityElement.flatten(): List<JsonEntityPrimitive> {
    val output = mutableListOf<JsonEntityPrimitive>()
    val stack = ArrayDeque<JsonEntityElement>()
    stack.add(this)
    while (stack.isNotEmpty()) {
        when (val current = stack.removeLast()) {
            is JsonEntityNull -> { /* ignore */ }
            is JsonEntityPrimitive -> output.add(current)
            is JsonEntityList -> stack.addAll(current.asReversed())
        }
    }
    return output
}

fun JsonEntityElement.toJsonElement(): JsonElement = when (this) {
    is JsonEntityNull -> JsonNull
    is JsonEntityPrimitive -> this.content.jsonElement
    is JsonEntityList -> JsonArray(this.map { it.toJsonElement() })
}