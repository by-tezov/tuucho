package com.tezov.tuucho.core.data.repository.parser.breaker._system

import com.tezov.tuucho.core.data.repository.exception.DataException
import com.tezov.tuucho.core.data.repository.parser._system.JsonArrayNode
import com.tezov.tuucho.core.data.repository.parser._system.JsonElementNode
import com.tezov.tuucho.core.data.repository.parser._system.JsonObjectNode
import com.tezov.tuucho.core.domain.business.di.TuuchoKoinComponent
import com.tezov.tuucho.core.domain.business.jsonSchema._system.SchemaScope
import com.tezov.tuucho.core.domain.business.jsonSchema._system.onScope
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.IdSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import com.tezov.tuucho.core.domain.tool.json.find
import com.tezov.tuucho.core.domain.tool.json.replaceOrInsert
import com.tezov.tuucho.core.domain.tool.json.toPath
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import org.koin.core.component.KoinComponent

abstract class AbstractBreaker : MatcherBreakerProtocol, TuuchoKoinComponent {
    protected open val matchers: List<MatcherBreakerProtocol> = emptyList()
    protected open val childProcessors: List<AbstractBreaker> = emptyList()

    private fun <T> List<T>.singleOrThrow(path: JsonElementPath): T? {
        if (size > 1) throw DataException.Default("Only one child processor can accept the element at path $path")
        return firstOrNull()
    }

    override fun accept(
        path: JsonElementPath,
        element: JsonElement,
    ) = matchers.any { it.accept(path, element) }

    fun process(
        path: JsonElementPath,
        element: JsonElement,
    ): JsonElementNode = with(element.find(path)) {
        when (this) {
            is JsonArray -> processArray()
            is JsonObject -> processObject(path, element)
            else -> throw DataException.Default("Primitive to JsonEntity is not allowed by design")
        }
    }

    private fun JsonArray.processArray() = map { entry ->
        (entry as? JsonObject)
            ?: throw DataException.Default("By design element inside array must be object, so there is surely something missing in the rectifier for $entry ")
        entry.processObject("".toPath(), entry)
    }.let { JsonArrayNode(it) }

    private fun JsonObject.processObject(
        path: JsonElementPath,
        element: JsonElement,
    ): JsonObjectNode {
        if (childProcessors.isEmpty()) {
            return JsonObjectNode(this)
        }
        val mapEntity = mutableMapOf<String, JsonElementNode>()
        keys.forEach { childKey ->
            val childPath = path.child(childKey)
            childProcessors
                .filter { it.accept(childPath, element) }
                .singleOrThrow(path)
                ?.let {
                    mapEntity[childKey] =
                        it.process(childPath, element)
                }
        }
        return processObject(mapEntity)
    }

    private fun JsonObject.processObject(
        map: Map<String, JsonElementNode>,
    ): JsonObjectNode {
        if (map.isEmpty()) {
            return JsonObjectNode(this)
        }
        var _element = this as JsonElement
        val children = buildList {
            map.forEach { (key, value) ->
                val newValue = when (value) {
                    is JsonArrayNode -> value.map { entry ->
                        (entry as? JsonObjectNode)?.let {
                            add(entry)
                            entry.toJsonObjectRef()
                        }
                            ?: throw DataException.Default("By design element inside array must be object")
                    }.let(::JsonArray)

                    is JsonObjectNode -> {
                        add(value)
                        value.toJsonObjectRef()
                    }
                }
                _element = _element.replaceOrInsert(key.toPath(), newValue)
            }
        }
        return JsonObjectNode(_element.jsonObject)
            .apply { this.children = children }
    }

    private fun JsonObjectNode.toJsonObjectRef() = JsonNull.withScope(::SchemaScope).apply {
        withScope(TypeSchema::Scope).apply {
            self = content.withScope(TypeSchema::Scope).self
        }
        withScope(IdSchema::Scope).apply {
            self = onScope(IdSchema::Scope).apply {
                source = content.onScope(IdSchema::Scope).value
            }.collect()
        }
    }.collect()

}