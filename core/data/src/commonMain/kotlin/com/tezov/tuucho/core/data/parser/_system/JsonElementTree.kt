package com.tezov.tuucho.core.data.parser._system

import com.tezov.tuucho.core.data.database.entity.JsonObjectEntity
import com.tezov.tuucho.core.data.exception.DataException
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement

sealed class JsonElementTree

class JsonEntityObjectTree(val content: JsonObjectEntity) : JsonElementTree() {

    var children: List<JsonElementTree>? = null

    override fun equals(other: Any?) = content == other
    override fun hashCode() = content.hashCode()
    override fun toString() = content.toString()
}

fun JsonObjectEntity.toTree() = JsonEntityObjectTree(this)

class JsonEntityArrayTree(private val content: List<JsonElementTree>) : JsonElementTree(),
    List<JsonElementTree> by content {
    override fun equals(other: Any?) = content == other
    override fun hashCode() = content.hashCode()
    override fun toString() = content
        .joinToString(prefix = "[", postfix = "]", separator = ",")
}

fun List<JsonElementTree>.toTree() = JsonEntityArrayTree(this)

val JsonElementTree.jsonEntityObjectTree
    get() = this as? JsonEntityObjectTree ?: throw DataException.Default("JsonEntityPrimitive")

val JsonElementTree.jsonEntityArrayTree
    get() = this as? JsonEntityArrayTree ?: throw DataException.Default("JsonEntityList")

fun JsonElementTree.flatten(): List<JsonEntityObjectTree> {
    val output = mutableListOf<JsonEntityObjectTree>()
    val stack = ArrayDeque<JsonElementTree>()
    stack.add(this)
    while (stack.isNotEmpty()) {
        when (val current = stack.removeLast()) {
            is JsonEntityObjectTree -> {
                output.add(current)
                val children = current.children
                if (children?.isNotEmpty() == true) {
                    current.children = null
                    stack.addAll(children.asReversed())
                }
            }

            is JsonEntityArrayTree -> stack.addAll(current.asReversed())
        }
    }
    return output
}

fun JsonElementTree.toJsonElement(): JsonElement = when (this) {
    is JsonEntityObjectTree -> this.content.jsonObject
    is JsonEntityArrayTree -> JsonArray(this.map { it.toJsonElement() })
}
