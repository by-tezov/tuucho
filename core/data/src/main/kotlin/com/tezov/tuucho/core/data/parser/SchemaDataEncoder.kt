package com.tezov.tuucho.core.data.parser

import com.tezov.tuucho.core.data.cache.entity.JsonEntity
import com.tezov.tuucho.core.data.parser._schema._common.header.HeaderIdSchemaData.Companion.idObject
import com.tezov.tuucho.core.data.parser._schema._common.header.HeaderIdSchemaData.Companion.idPutObject
import com.tezov.tuucho.core.data.parser._schema._common.header.HeaderIdSchemaData.Companion.idSourceOrNull
import com.tezov.tuucho.core.data.parser._schema._common.header.HeaderIdSchemaData.Companion.idValue
import com.tezov.tuucho.core.data.parser._schema._common.header.HeaderTypeSchemaData.Companion.type
import com.tezov.tuucho.core.data.parser._schema._common.header.HeaderTypeSchemaData.Companion.typePut
import com.tezov.tuucho.core.data.parser._system.IdGenerator
import com.tezov.tuucho.core.data.parser._system.JsonElementPath
import com.tezov.tuucho.core.data.parser._system.JsonEntityElement
import com.tezov.tuucho.core.data.parser._system.JsonEntityList
import com.tezov.tuucho.core.data.parser._system.JsonEntityNull
import com.tezov.tuucho.core.data.parser._system.JsonEntityPrimitive
import com.tezov.tuucho.core.data.parser._system.find
import com.tezov.tuucho.core.data.parser._system.isRef
import com.tezov.tuucho.core.data.parser._system.replace
import com.tezov.tuucho.core.data.parser._system.toJsonElement
import com.tezov.tuucho.core.data.parser._system.toPath
import com.tezov.tuucho.core.data.parser.encoder.EncoderConfig
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

abstract class SchemaDataEncoder : SchemaDataMatcher, KoinComponent {

    private val idGenerator: IdGenerator by inject()

    protected open val matchers: List<SchemaDataMatcher> = emptyList()
    protected open val childProcessors: List<SchemaDataEncoder> = emptyList()

    override fun accept(
        path: JsonElementPath, element: JsonElement
    ) = matchers.any { it.accept(path, element) }

    protected open fun JsonObject.toJsonObjectEntity(config: EncoderConfig): JsonEntityPrimitive =
        JsonEntityPrimitive(if (isRef) {
            JsonEntity(
                type = type,
                url = config.url,
                id = idObject.idValue,
                idFrom = idObject.idSourceOrNull,
                jsonElement = this
            )
        } else {
            val id = idGenerator.generate()
            JsonEntity(
                type = type,
                url = config.url,
                id = id,
                idFrom = idObject.idValue,
                jsonElement = JsonObject(mutableMapOf<String, JsonElement>().apply {
                    idPutObject(id, this@toJsonObjectEntity.idObject.idValue)
                    typePut(this@toJsonObjectEntity.type)
                })
            ).apply {
                children = listOf(
                    JsonEntity(
                        type = this@toJsonObjectEntity.type,
                        url = config.url,
                        id = this@toJsonObjectEntity.idObject.idValue,
                        idFrom = this@toJsonObjectEntity.idObject.idSourceOrNull,
                        jsonElement = this@toJsonObjectEntity
                    )
                )
            }
        })

    protected open fun JsonArray.toJsonObjectEntity(config: EncoderConfig): JsonEntityList {
        return JsonEntityList(map { it.jsonObject.toJsonObjectEntity(config) })
    }

    protected open fun JsonObject.toJsonObjectEntity(
        map: Map<String, JsonEntityElement>,
        config: EncoderConfig
    ): JsonEntityElement {
        if (map.isEmpty()) {
            return toJsonObjectEntity(config)
        }
        var element = this as JsonElement
        val output = mutableListOf<JsonEntityElement>()
        map.forEach { (key, value) ->
            element = element.replace(
                key.toPath(),
                value.toJsonElement()
            )
            output.add(value)
        }
        output.add(element.jsonObject.toJsonObjectEntity(config))
        return JsonEntityList(output)
    }

    private fun <T> Sequence<T>.singleOrThrow(path: JsonElementPath): T? {
        val list = this.toList()
        if (list.size > 1)
            throw IllegalStateException("More than one child processor accepted the element at path $path")
        return list.firstOrNull()
    }

    open fun process(
        path: JsonElementPath, element: JsonElement, config: EncoderConfig
    ): JsonEntityElement {
        when (val current = element.find(path)) {
            is JsonArray -> {
                if (childProcessors.isEmpty()) {
                    return current.jsonArray.toJsonObjectEntity(config)
                }
                return childProcessors.asSequence()
                    .filter { it.accept(path, element) }
                    .singleOrThrow(path)
                    ?.process(path, element, config)
                    ?: JsonEntityNull

            }

            is JsonObject -> {
                if (childProcessors.isEmpty()) {
                    return current.jsonObject.toJsonObjectEntity(config)
                }
                val mapEntity = mutableMapOf<String, JsonEntityElement>()
                current.jsonObject.keys.forEach { childKey ->
                    val childPath = path.child(childKey)
                    childProcessors.asSequence()
                        .filter { it.accept(childPath, element) }
                        .singleOrThrow(path)
                        ?.let {
                            mapEntity[childKey] = it.process(childPath, element, config)

                        }
                }
                return current.toJsonObjectEntity(mapEntity, config)
            }

            else -> throw IllegalStateException("primitive to JsonEntity is not possible by design")
        }
    }
}