package com.tezov.tuucho.core.data.repository.parser._system

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

sealed class JsonElementNode

class JsonObjectNode(val content: JsonObject) : JsonElementNode() {

    var children: List<JsonElementNode>? = null

    override fun equals(other: Any?) = content == other
    override fun hashCode() = content.hashCode()
    override fun toString() = content.toString()
}

class JsonArrayNode(private val content: List<JsonElementNode>) : JsonElementNode(),
    List<JsonElementNode> by content {
    override fun equals(other: Any?) = content == other
    override fun hashCode() = content.hashCode()
    override fun toString() = content
        .joinToString(prefix = "[", postfix = "]", separator = ",")
}

fun JsonElementNode.flatten(): List<JsonObjectNode> {
    return buildList {
        val stack = ArrayDeque<JsonElementNode>()
        stack.add(this@flatten)
        while (stack.isNotEmpty()) {
            when (val current = stack.removeLast()) {
                is JsonObjectNode -> {
                    add(current)
                    val children = current.children
                    if (children?.isNotEmpty() == true) {
                        current.children = null
                        stack.addAll(children.asReversed())
                    }
                }

                is JsonArrayNode -> stack.addAll(current.asReversed())
            }
        }
    }
}

fun JsonElementNode.toJsonElement(): JsonElement = when (this) {
    is JsonObjectNode -> this.content
    is JsonArrayNode -> JsonArray(this.map { it.toJsonElement() })
}
