package com.tezov.tuucho.core.data.parser.assembler

import com.tezov.tuucho.core.data.database.dao.JsonObjectQueries
import com.tezov.tuucho.core.data.parser._system.MatcherProtocol
import com.tezov.tuucho.core.domain._system.JsonElementPath
import com.tezov.tuucho.core.domain._system.booleanOrNull
import com.tezov.tuucho.core.domain._system.find
import com.tezov.tuucho.core.domain._system.replace
import com.tezov.tuucho.core.domain._system.toPath
import com.tezov.tuucho.core.domain.model.schema._system.Schema.Companion.schema
import com.tezov.tuucho.core.domain.model.schema._system.SchemaScope
import com.tezov.tuucho.core.domain.model.schema.material.IdSchema
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

abstract class Assembler : MatcherProtocol, KoinComponent {
    abstract val dataBaseType: String

    protected val jsonObjectQueries: JsonObjectQueries by inject()

    protected open val matchers: List<MatcherProtocol> = emptyList()
    protected open val childProcessors: List<Assembler> = emptyList()

    override fun accept(
        path: JsonElementPath,
        element: JsonElement,
    ) = matchers.any { it.accept(path, element) }

    fun process(
        path: JsonElementPath,
        element: JsonElement,
        extraData: ExtraDataAssembler,
    ) = with(element.find(path)) {
        when (this) {
            is JsonArray -> processArray(path, element, extraData)
            is JsonObject -> processObject(path, element, extraData)
            is JsonPrimitive -> processPrimitive(path, element, extraData)
        }
    }

    private fun JsonArray.processArray(
        path: JsonElementPath,
        element: JsonElement,
        extraData: ExtraDataAssembler,
    ) = element.replace(
        path, map {
            (it as? JsonObject)?.assembleObject("".toPath(), it, extraData)
                ?: error("by design element inside array must be object")
        }.let(::JsonArray)
    )

    private fun JsonObject.processObject(
        path: JsonElementPath,
        element: JsonElement,
        extraData: ExtraDataAssembler,
    ) = assembleObject(path, element, extraData)

    private fun JsonPrimitive.processPrimitive(
        path: JsonElementPath,
        element: JsonElement,
        extraData: ExtraDataAssembler,
    ) = null

    private fun <T> List<T>.singleOrThrow(path: JsonElementPath): T? {
        if (size > 1) error("Only one child processor can accept the element at path $path")
        return firstOrNull()
    }

    protected open fun List<JsonObject>.rectify(
        path: JsonElementPath,
        element: JsonElement,
        extraData: ExtraDataAssembler,
    ): List<JsonObject>? = null

    protected open fun JsonObject.rectify(
        path: JsonElementPath,
        element: JsonElement,
        extraData: ExtraDataAssembler,
    ): JsonObject? = null

    private fun JsonObject.assembleObject(
        path: JsonElementPath,
        element: JsonElement,
        extraData: ExtraDataAssembler,
    ): JsonElement {
        var current = retrieveAllRef(extraData.url, dataBaseType)?.let { refs ->
            (refs.rectify(path, element, extraData) ?: refs).merge()
        } ?: rectify(path, element, extraData) ?: this
        current = current.schema().withScope(IdSchema::Scope).apply {
            // make id a primitive value since all has been resolved
            self = onScope(IdSchema::Scope).value.let(::JsonPrimitive)
        }.collect()
        var _element = element.replace(path, current)
        if (childProcessors.isNotEmpty()) {
            current.keys.forEach { childKey ->
                val childPath = path.child(childKey)
                childProcessors
                    .filter { it.accept(childPath, _element) }
                    .singleOrThrow(path)
                    ?.process(childPath, _element, extraData)
                    ?.also { _element = it }
            }
        }
        return _element
    }

    private fun JsonObject.retrieveAllRef(
        url: String,
        type: String,
    ): List<JsonObject>? {
        schema().onScope(IdSchema::Scope).source ?: return null
        var currentEntry = this
        val entries = mutableListOf(currentEntry)
        do {
            val idRef = currentEntry.schema().onScope(IdSchema::Scope).source
            val entity = idRef?.let { ref ->
                jsonObjectQueries.find(type = type, url = url, id = ref)
                    ?: jsonObjectQueries.findShared(type = type, id = ref)
            }
            if (entity != null) {
                currentEntry = entity.jsonObject
                entries.add(currentEntry)
            }
        } while (idRef != null && entity != null)
        return entries
    }

    private fun List<JsonObject>.merge() = when (this.size) {
        1 -> first()

        else -> {
            JsonNull.schema().withScope(::SchemaScope).apply {
                for (entry in this@merge.asReversed()) {
                    merge(entry)
                }
            }.collect()
        }
    }

    private fun SchemaScope.merge(other: JsonObject) {
        for ((key, value) in other) {
            val currentValue = this[key]
            this[key] = when {
                currentValue is JsonObject && value is JsonObject -> {
                    JsonNull.schema().withScope(::SchemaScope).apply {
                        when {
                            key == IdSchema.root -> mergeId(value)
                            else -> merge(value)
                        }
                    }.collect()
                }

                else -> value
            }
        }
    }

    private fun SchemaScope.mergeId(other: JsonObject) {
        onScope(IdSchema::Scope)
            .takeIf {
                val otherIdAutoGenerated = (other[IdSchema.root] as? JsonObject)
                    ?.get(IdSchema.Key.id_auto_generated).booleanOrNull
                otherIdAutoGenerated == true && it.idAutoGenerated != true
            }
            ?.apply {
                // id auto generated is weak against user id, so we remove it
                other.toMutableMap()
                    .apply { remove(IdSchema.root) }
                    .let { merge(JsonObject(it)) }
            } ?: merge(other)
    }
}