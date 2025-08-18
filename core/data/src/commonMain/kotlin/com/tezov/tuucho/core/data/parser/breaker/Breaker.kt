package com.tezov.tuucho.core.data.parser.breaker

import com.tezov.tuucho.core.data.exception.DataException
import com.tezov.tuucho.core.data.parser._system.JsonElementTree
import com.tezov.tuucho.core.data.parser._system.JsonEntityArrayTree
import com.tezov.tuucho.core.data.parser._system.JsonEntityObjectTree
import com.tezov.tuucho.core.data.parser.breaker._system.JsonEntityObjectTreeProducerProtocol
import com.tezov.tuucho.core.data.parser.breaker._system.MatcherBreakerProtocol
import com.tezov.tuucho.core.domain.business.jsonSchema._system.SchemaScope
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

abstract class Breaker : MatcherBreakerProtocol, KoinComponent {
    protected open val matchers: List<MatcherBreakerProtocol> = emptyList()
    protected open val childProcessors: List<Breaker> = emptyList()

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
        jsonEntityObjectTreeProducer: JsonEntityObjectTreeProducerProtocol,
    ): JsonElementTree = with(element.find(path)) {
        when (this) {
            is JsonArray -> processArray(jsonEntityObjectTreeProducer)
            is JsonObject -> processObject(path, element, jsonEntityObjectTreeProducer)
            else -> throw DataException.Default("Primitive to JsonEntity is not allowed by design")
        }
    }

    private fun JsonArray.processArray(
        jsonEntityObjectTreeProducer: JsonEntityObjectTreeProducerProtocol,
    ) = map { entry ->
        (entry as? JsonObject)
            ?: throw DataException.Default("By design element inside array must be object, so there is surely something missing in the rectifier for $entry ")
        entry.processObject("".toPath(), entry, jsonEntityObjectTreeProducer)
    }.let { JsonEntityArrayTree(it) }

    private fun JsonObject.processObject(
        path: JsonElementPath,
        element: JsonElement,
        jsonEntityObjectTreeProducer: JsonEntityObjectTreeProducerProtocol,
    ): JsonEntityObjectTree {
        if (childProcessors.isEmpty()) {
            return jsonEntityObjectTreeProducer.invoke(this)
        }
        val mapEntity = mutableMapOf<String, JsonElementTree>()
        keys.forEach { childKey ->
            val childPath = path.child(childKey)
            childProcessors
                .filter { it.accept(childPath, element) }
                .singleOrThrow(path)
                ?.let {
                    mapEntity[childKey] =
                        it.process(childPath, element, jsonEntityObjectTreeProducer)
                }
        }
        return processObject(mapEntity, jsonEntityObjectTreeProducer)
    }

    private fun JsonObject.processObject(
        map: Map<String, JsonElementTree>,
        jsonEntityObjectTreeProducer: JsonEntityObjectTreeProducerProtocol,
    ): JsonEntityObjectTree {
        if (map.isEmpty()) {
            return jsonEntityObjectTreeProducer.invoke(this)
        }
        var _element = this as JsonElement
        val children = buildList {
            map.forEach { (key, value) ->
                val newValue = when (value) {
                    is JsonEntityArrayTree -> value.map { entry ->
                        (entry as? JsonEntityObjectTree)?.let {
                            add(entry)
                            entry.toJsonObjectRef()
                        }
                            ?: throw DataException.Default("By design element inside array must be object")
                    }.let(::JsonArray)

                    is JsonEntityObjectTree -> {
                        add(value)
                        value.toJsonObjectRef()
                    }
                }
                _element = _element.replaceOrInsert(key.toPath(), newValue)
            }
        }
        return _element.jsonObject
            .let { jsonEntityObjectTreeProducer.invoke(it) }
            .apply { this.children = children }
    }

    private fun JsonEntityObjectTree.toJsonObjectRef() = JsonNull.withScope(::SchemaScope).apply {
        withScope(TypeSchema::Scope).apply {
            self = content.type
        }
        withScope(IdSchema::Scope).apply {
            self = onScope(IdSchema::Scope).apply {
                source = content.id
            }.collect()
        }
    }.collect()


}