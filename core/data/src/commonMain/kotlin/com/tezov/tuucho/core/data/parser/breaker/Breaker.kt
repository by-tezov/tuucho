package com.tezov.tuucho.core.data.parser.breaker

import com.tezov.tuucho.core.data.database.entity.JsonObjectEntity
import com.tezov.tuucho.core.data.exception.DataException
import com.tezov.tuucho.core.data.parser._system.JsonElementTree
import com.tezov.tuucho.core.data.parser._system.JsonEntityArrayTree
import com.tezov.tuucho.core.data.parser._system.JsonEntityObjectTree
import com.tezov.tuucho.core.data.parser._system.MatcherProtocol
import com.tezov.tuucho.core.data.parser._system.toTree
import com.tezov.tuucho.core.data.parser.breaker._system.ArgumentBreaker
import com.tezov.tuucho.core.domain._system.JsonElementPath
import com.tezov.tuucho.core.domain._system.find
import com.tezov.tuucho.core.domain._system.replaceOrInsert
import com.tezov.tuucho.core.domain._system.toPath
import com.tezov.tuucho.core.domain.model.schema._system.SchemaScope
import com.tezov.tuucho.core.domain.model.schema._system.onScope
import com.tezov.tuucho.core.domain.model.schema._system.withScope
import com.tezov.tuucho.core.domain.model.schema.material.IdSchema
import com.tezov.tuucho.core.domain.model.schema.material.TypeSchema
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import org.koin.core.component.KoinComponent

abstract class Breaker : MatcherProtocol, KoinComponent {

    protected open val matchers: List<MatcherProtocol> = emptyList()
    protected open val childProcessors: List<Breaker> = emptyList()

    override fun accept(
        path: JsonElementPath,
        element: JsonElement,
    ) = matchers.any { it.accept(path, element) }

    fun process(
        path: JsonElementPath,
        element: JsonElement,
        argument: ArgumentBreaker,
    ): JsonElementTree = with(element.find(path)) {
        when (this) {
            is JsonArray -> processArray(path, element, argument)
            is JsonObject -> processObject(path, element, argument)
            else -> throw DataException.Default("primitive to JsonEntity is not allowed by design")
        }
    }

    private fun JsonArray.processArray(
        path: JsonElementPath,
        element: JsonElement,
        argument: ArgumentBreaker,
    ) = childProcessors
        .takeIf { it.isNotEmpty() }
        ?.filter { it.accept(path, element) }
        ?.singleOrThrow(path)
        ?.process(path, element, argument)
        ?: toJsonEntityArray(argument)

    private fun JsonObject.processObject(
        path: JsonElementPath,
        element: JsonElement,
        argument: ArgumentBreaker,
    ): JsonEntityObjectTree {
        if (childProcessors.isEmpty()) {
            return toTree(argument)
        }
        val mapEntity = mutableMapOf<String, JsonElementTree>()
        keys.forEach { childKey ->
            val childPath = path.child(childKey)
            childProcessors
                .filter { it.accept(childPath, element) }
                .singleOrThrow(path)
                ?.let {
                    mapEntity[childKey] = it.process(childPath, element, argument)
                }
        }
        return toTree(mapEntity, argument)
    }

    private fun <T> List<T>.singleOrThrow(path: JsonElementPath): T? {
        if (size > 1) throw DataException.Default("Only one child processor can accept the element at path $path")
        return firstOrNull()
    }

    private fun JsonArray.toJsonEntityArray(
        argument: ArgumentBreaker,
    ) = map { entry ->
        (entry as? JsonObject)?.toTree(argument)
            ?: throw DataException.Default("by design element inside array must be object, so there is surely something missing in the rectifier for $entry ")
    }.toTree()

    private fun JsonObject.toTree(
        argument: ArgumentBreaker,
    ): JsonEntityObjectTree {
        val type = withScope(TypeSchema::Scope).self
        val (id, idFrom) = onScope(IdSchema::Scope)
            .let { it.value to it.source }
        return JsonObjectEntity(
            type = type
                ?: throw DataException.Default("Should not be possible, so there is surely something missing in the rectifier for $this"),
            url = argument.url,
            id = id
                ?: throw DataException.Default("Should not be possible, so there is surely something missing in the rectifier for $this"),
            idFrom = idFrom,
            jsonObject = this@toTree
        ).toTree()
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

    private fun JsonObject.toTree(
        map: Map<String, JsonElementTree>,
        argument: ArgumentBreaker,
    ): JsonEntityObjectTree {
        if (map.isEmpty()) {
            return this@toTree.toTree(argument)
        }
        var _element = this as JsonElement
        val output = mutableListOf<JsonElementTree>()
        map.forEach { (key, value) ->
            val newValue = when (value) {
                is JsonEntityArrayTree -> value.map { entry ->
                    (entry as? JsonEntityObjectTree)?.let {
                        output.add(entry)
                        entry.toJsonObjectRef()
                    }
                        ?: throw DataException.Default("by design element inside array must be object")
                }.let(::JsonArray)

                is JsonEntityObjectTree -> {
                    output.add(value)
                    value.toJsonObjectRef()
                }
            }
            _element = _element.replaceOrInsert(key.toPath(), newValue)
        }
        return _element.jsonObject
            .toTree(argument)
            .apply { children = output }
    }
}