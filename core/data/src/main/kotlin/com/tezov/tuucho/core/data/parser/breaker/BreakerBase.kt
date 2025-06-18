package com.tezov.tuucho.core.data.parser.breaker

import com.tezov.tuucho.core.data.cache.entity.JsonEntity
import com.tezov.tuucho.core.data.parser._schema.header.HeaderIdSchema.Companion.idObject
import com.tezov.tuucho.core.data.parser._schema.header.HeaderIdSchema.Companion.idPutObject
import com.tezov.tuucho.core.data.parser._schema.header.HeaderIdSchema.Companion.idSourceOrNull
import com.tezov.tuucho.core.data.parser._schema.header.HeaderIdSchema.Companion.idValue
import com.tezov.tuucho.core.data.parser._schema.header.HeaderTypeSchema.Companion.type
import com.tezov.tuucho.core.data.parser._schema.header.HeaderTypeSchema.Companion.typePut
import com.tezov.tuucho.core.data.parser._system.JsonElementPath
import com.tezov.tuucho.core.data.parser._system.JsonEntityArray
import com.tezov.tuucho.core.data.parser._system.JsonEntityElement
import com.tezov.tuucho.core.data.parser._system.JsonEntityObject
import com.tezov.tuucho.core.data.parser._system.Matcher
import com.tezov.tuucho.core.data.parser._system.find
import com.tezov.tuucho.core.data.parser._system.replace
import com.tezov.tuucho.core.data.parser._system.toJsonEntityArray
import com.tezov.tuucho.core.data.parser._system.toJsonEntityObject
import com.tezov.tuucho.core.data.parser._system.toPath
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject

abstract class BreakerBase : Breaker {

    override val matchers: List<Matcher> = emptyList()
    override val childProcessors: List<Breaker> = emptyList()

    private fun <T> Sequence<T>.singleOrThrow(path: JsonElementPath): T? {
        val list = this.toList()
        if (list.size > 1)
            throw IllegalStateException("Only one child processor can accept the element at path $path")
        return list.firstOrNull()
    }

    private fun JsonArray.toJsonEntityArray(
        extraData: ExtraDataBreaker
    ) = map { entry ->
        (entry as? JsonObject)?.toJsonObjectEntity(extraData)
            ?: throw IllegalStateException("by design element inside array must be object")
    }.toJsonEntityArray()

    private fun JsonObject.toJsonObjectEntity(
        extraData: ExtraDataBreaker
    ): JsonEntityObject = JsonEntity(
        type = type,
        url = extraData.url,
        id = idObject.idValue,
        idFrom = idObject.idSourceOrNull,
        jsonObject = this
    ).toJsonEntityObject()

    private fun JsonEntityObject.toJsonObjectRef(): JsonObject {
        val content = content
        return JsonObject(mutableMapOf<String, JsonElement>().apply {
            idPutObject(null, content.id)
            typePut(content.type)
        })
    }

    private fun JsonObject.toJsonObjectEntity(
        map: Map<String, JsonEntityElement>,
        extraData: ExtraDataBreaker
    ): JsonEntityObject {
        if (map.isEmpty()) {
            return toJsonObjectEntity(extraData)
        }
        var _element = this as JsonElement
        val output = mutableListOf<JsonEntityElement>()
        map.forEach { (key, value) ->
            val newValue = when (value) {
                is JsonEntityArray -> value.map { entry ->
                    (entry as? JsonEntityObject)?.let {
                        output.add(entry)
                        entry.toJsonObjectRef()
                    }
                        ?: throw IllegalStateException("by design element inside array must be object")
                }.let(::JsonArray)

                is JsonEntityObject -> {
                    output.add(value)
                    value.toJsonObjectRef()
                }
            }
            _element = _element.replace(key.toPath(), newValue)
        }
        return _element.jsonObject
            .toJsonObjectEntity(extraData)
            .apply { children = output }
    }

    override fun process(
        path: JsonElementPath,
        element: JsonElement,
        extraData: ExtraDataBreaker
    ) = with(element.find(path)) {
        when (this) {
            is JsonArray -> processArray(path, element, extraData)
            is JsonObject -> processObject(path, element, extraData)
            else -> throw IllegalStateException("primitive to JsonEntity is not allowed by design")
        }
    }

    private fun JsonArray.processArray(
        path: JsonElementPath,
        element: JsonElement,
        extraData: ExtraDataBreaker
    ) = childProcessors
        .takeIf { it.isNotEmpty() }
        ?.asSequence()
        ?.filter { it.accept(path, element) }
        ?.singleOrThrow(path)
        ?.process(path, element, extraData)
        ?: toJsonEntityArray(extraData)

    private fun JsonObject.processObject(
        path: JsonElementPath,
        element: JsonElement,
        extraData: ExtraDataBreaker
    ): JsonEntityObject {
        if (childProcessors.isEmpty()) {
            return toJsonObjectEntity(extraData)
        }
        val mapEntity = mutableMapOf<String, JsonEntityElement>()
        keys.forEach { childKey ->
            val childPath = path.child(childKey)
            childProcessors.asSequence()
                .filter { it.accept(childPath, element) }
                .singleOrThrow(path)
                ?.let {
                    mapEntity[childKey] = it.process(childPath, element, extraData)
                }
        }
        return toJsonObjectEntity(mapEntity, extraData)
    }
}