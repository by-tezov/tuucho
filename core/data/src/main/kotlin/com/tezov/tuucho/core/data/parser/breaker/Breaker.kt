package com.tezov.tuucho.core.data.parser.breaker

import com.tezov.tuucho.core.data.database.entity.JsonEntity
import com.tezov.tuucho.core.data.parser._system.JsonEntityArray
import com.tezov.tuucho.core.data.parser._system.JsonEntityElement
import com.tezov.tuucho.core.data.parser._system.JsonEntityObject
import com.tezov.tuucho.core.data.parser._system.Matcher
import com.tezov.tuucho.core.data.parser._system.toJsonEntityArray
import com.tezov.tuucho.core.data.parser._system.toJsonEntityObject
import com.tezov.tuucho.core.domain._system.JsonElementPath
import com.tezov.tuucho.core.domain._system.find
import com.tezov.tuucho.core.domain._system.replace
import com.tezov.tuucho.core.domain._system.toPath
import com.tezov.tuucho.core.domain.schema.common.IdSchema.Companion.idObject
import com.tezov.tuucho.core.domain.schema.common.IdSchema.Companion.idPutObject
import com.tezov.tuucho.core.domain.schema.common.IdSchema.Companion.idSourceOrNull
import com.tezov.tuucho.core.domain.schema.common.IdSchema.Companion.idValue
import com.tezov.tuucho.core.domain.schema.common.TypeSchema.Companion.type
import com.tezov.tuucho.core.domain.schema.common.TypeSchema.Companion.typePut
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import org.koin.core.component.KoinComponent

abstract class Breaker : Matcher, KoinComponent {

    protected open val matchers: List<Matcher> = emptyList()
    protected open val childProcessors: List<Breaker> = emptyList()

    override fun accept(
        path: JsonElementPath,
        element: JsonElement
    ) = matchers.any { it.accept(path, element) }

    fun process(
        path: JsonElementPath,
        element: JsonElement,
        extraData: ExtraDataBreaker
    ): JsonEntityElement = with(element.find(path)) {
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
            childProcessors
                .filter { it.accept(childPath, element) }
                .singleOrThrow(path)
                ?.let {
                    mapEntity[childKey] = it.process(childPath, element, extraData)
                }
        }
        return toJsonObjectEntity(mapEntity, extraData)
    }

    private fun <T> List<T>.singleOrThrow(path: JsonElementPath): T? {
        if (size > 1)
            throw IllegalStateException("Only one child processor can accept the element at path $path")
        return firstOrNull()
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
}