package com.tezov.tuucho.core.data.parser.breaker

import android.util.MalformedJsonException
import com.tezov.tuucho.core.data.cache.entity.JsonEntity
import com.tezov.tuucho.core.data.parser._schema._common.header.HeaderIdSchema.Companion.idObject
import com.tezov.tuucho.core.data.parser._schema._common.header.HeaderIdSchema.Companion.idPutObject
import com.tezov.tuucho.core.data.parser._schema._common.header.HeaderIdSchema.Companion.idSourceOrNull
import com.tezov.tuucho.core.data.parser._schema._common.header.HeaderIdSchema.Companion.idValue
import com.tezov.tuucho.core.data.parser._schema._common.header.HeaderTypeSchema.Companion.type
import com.tezov.tuucho.core.data.parser._schema._common.header.HeaderTypeSchema.Companion.typePut
import com.tezov.tuucho.core.data.parser._system.Breaker
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
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject

abstract class BreakerBase : Breaker {

    override val matchers: List<Matcher> = emptyList()
    override val childProcessors: List<Breaker> = emptyList()

    private fun JsonArray.toJsonEntityArray(
        extraData: ExtraDataBreaker
    ): JsonEntityArray = map { it.jsonObject.toJsonObjectEntity(extraData) }
        .toJsonEntityArray()

    private fun JsonObject.toJsonObjectEntity(
        extraData: ExtraDataBreaker
    ): JsonEntityObject = JsonEntity(
        type = type,
        url = extraData.url,
        id = idObject.idValue,
        idFrom = idObject.idSourceOrNull,
        jsonElement = this
    ).toJsonEntityObject()

    private fun JsonObject.toJsonObjectEntity(
        map: Map<String, JsonEntityElement>,
        extraData: ExtraDataBreaker
    ): JsonEntityObject {
        if (map.isEmpty()) {
            return toJsonObjectEntity(extraData)
        }
        var current = this as JsonElement
        val output = mutableListOf<JsonEntityElement>()
        map.forEach { (key, value) ->
            current = current.replace(
                key.toPath(),
                when (value) {
                    is JsonEntityArray -> {
                        JsonArray(mutableListOf<JsonElement>().apply {
                            value.map {
                                when (it) {
                                    is JsonEntityArray -> throw MalformedJsonException("by design array inside array are not allowed")
                                    is JsonEntityObject -> {
                                        output.add(it)
                                        val content = it.content
                                        JsonObject(mutableMapOf<String, JsonElement>().apply {
                                            idPutObject(null, content.id)
                                            typePut(content.type)
                                        })
                                    }
                                }
                            }.also(this::addAll)
                        })
                    }

                    is JsonEntityObject -> {
                        output.add(value)
                        val content = value.content
                        JsonObject(mutableMapOf<String, JsonElement>().apply {
                            idPutObject(null, content.id)
                            typePut(content.type)
                        })
                    }
                }
            )
        }
        return current
            .jsonObject
            .toJsonObjectEntity(extraData)
            .apply { children = output }
    }

    private fun <T> Sequence<T>.singleOrThrow(path: JsonElementPath): T? {
        val list = this.toList()
        if (list.size > 1)
            throw IllegalStateException("Only one child processor can accept the element at path $path")
        return list.firstOrNull()
    }

    override fun process(
        path: JsonElementPath,
        element: JsonElement,
        extraData: ExtraDataBreaker
    ) = when (val current = element.find(path)) {
        is JsonArray -> {
            childProcessors.asSequence()
                .filter { it.accept(path, element) }
                .singleOrThrow(path)
                ?.process(path, element, extraData)
                ?: current.jsonArray.toJsonEntityArray(extraData)
        }

        is JsonObject -> {
            val mapEntity = mutableMapOf<String, JsonEntityElement>()
            current.jsonObject.keys.forEach { childKey ->
                val childPath = path.child(childKey)
                childProcessors.asSequence()
                    .filter { it.accept(childPath, element) }
                    .singleOrThrow(path)
                    ?.let {
                        mapEntity[childKey] = it.process(childPath, element, extraData)
                    }
            }
            current.toJsonObjectEntity(mapEntity, extraData)
        }

        else -> throw IllegalStateException("primitive to JsonEntity is not allowed by design")
    }
}