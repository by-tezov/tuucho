package com.tezov.tuucho.core.data.parser._system

import com.tezov.tuucho.core.data.database.entity.JsonObjectEntity
import com.tezov.tuucho.core.data.exception.DataException
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement

sealed class JsonElementTree

class JsonObjectEntityTree(val content: JsonObjectEntity) : JsonElementTree() {

    var children: List<JsonElementTree>? = null

    override fun equals(other: Any?) = content == other
    override fun hashCode() = content.hashCode()
    override fun toString() = content.toString()
}

class JsonArrayEntityTree(private val content: List<JsonElementTree>) : JsonElementTree(),
    List<JsonElementTree> by content {
    override fun equals(other: Any?) = content == other
    override fun hashCode() = content.hashCode()
    override fun toString() = content
        .joinToString(prefix = "[", postfix = "]", separator = ",")
}

val JsonElementTree.jsonObjectEntityTree
    get() = this as? JsonObjectEntityTree ?: throw DataException.Default("JsonEntityObjectTree")

val JsonElementTree.jsonArrayEntityTree
    get() = this as? JsonArrayEntityTree ?: throw DataException.Default("JsonEntityArrayTree")

fun JsonElementTree.flatten(): List<JsonObjectEntityTree> {
    return buildList {
        val stack = ArrayDeque<JsonElementTree>()
        stack.add(this@flatten)
        while (stack.isNotEmpty()) {
            when (val current = stack.removeLast()) {
                is JsonObjectEntityTree -> {
                    add(current)
                    val children = current.children
                    if (children?.isNotEmpty() == true) {
                        current.children = null
                        stack.addAll(children.asReversed())
                    }
                }

                is JsonArrayEntityTree -> stack.addAll(current.asReversed())
            }
        }
    }
}

fun JsonElementTree.toJsonElement(): JsonElement = when (this) {
    is JsonObjectEntityTree -> this.content.jsonObject
    is JsonArrayEntityTree -> JsonArray(this.map { it.toJsonElement() })
}
