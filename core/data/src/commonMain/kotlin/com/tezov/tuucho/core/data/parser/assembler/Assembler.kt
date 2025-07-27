package com.tezov.tuucho.core.data.parser.assembler

import com.tezov.tuucho.core.data.database.dao.JsonObjectQueries
import com.tezov.tuucho.core.data.exception.DataException
import com.tezov.tuucho.core.data.parser._system.MatcherProtocol
import com.tezov.tuucho.core.data.parser.assembler._system.ArgumentAssembler
import com.tezov.tuucho.core.domain._system.JsonElementPath
import com.tezov.tuucho.core.domain._system.find
import com.tezov.tuucho.core.domain._system.replaceOrInsert
import com.tezov.tuucho.core.domain._system.toPath
import com.tezov.tuucho.core.domain.model.schema._system.SchemaScope
import com.tezov.tuucho.core.domain.model.schema._system.onScope
import com.tezov.tuucho.core.domain.model.schema._system.withScope
import com.tezov.tuucho.core.domain.model.schema.material.IdSchema
import com.tezov.tuucho.core.domain.model.schema.material.SettingSchema
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
        argument: ArgumentAssembler,
    ) = with(element.find(path)) {
        when (this) {
            is JsonArray -> processArray(path, element, argument)
            is JsonObject -> processObject(path, element, argument)
            is JsonPrimitive -> processPrimitive(path, element, argument)
        }
    }

    private fun JsonArray.processArray(
        path: JsonElementPath,
        element: JsonElement,
        argument: ArgumentAssembler,
    ) = element.replaceOrInsert(
        path, map {
            (it as? JsonObject)?.assembleObject("".toPath(), it, argument)
                ?: throw DataException.Default("by design element inside array must be object")
        }.let(::JsonArray)
    )

    private fun JsonObject.processObject(
        path: JsonElementPath,
        element: JsonElement,
        argument: ArgumentAssembler,
    ) = assembleObject(path, element, argument)

    private fun JsonPrimitive.processPrimitive(
        path: JsonElementPath,
        element: JsonElement,
        argument: ArgumentAssembler,
    ) = null

    private fun <T> List<T>.singleOrThrow(path: JsonElementPath): T? {
        if (size > 1) throw DataException.Default("Only one child processor can accept the element at path $path")
        return firstOrNull()
    }

    protected open fun List<JsonObject>.rectify(
        path: JsonElementPath,
        element: JsonElement,
        argument: ArgumentAssembler,
    ): List<JsonObject>? = null

    protected open fun JsonObject.rectify(
        path: JsonElementPath,
        element: JsonElement,
        argument: ArgumentAssembler,
    ): JsonObject? = null

    private fun JsonObject.assembleObject(
        path: JsonElementPath,
        element: JsonElement,
        argument: ArgumentAssembler,
    ): JsonElement {
        val currentRectifiedAndResolved = retrieveAllRef(argument.url, dataBaseType)?.let {
            (it.rectify(path, element, argument) ?: it).merge()
        } ?: rectify(path, element, argument) ?: this
        var _element = (currentRectifiedAndResolved.udpateSetting(element) ?: element)
            .replaceOrInsert(path, currentRectifiedAndResolved)
        if (childProcessors.isNotEmpty()) {
            currentRectifiedAndResolved.keys.forEach { childKey ->
                val childPath = path.child(childKey)
                childProcessors
                    .filter { it.accept(childPath, _element) }
                    .singleOrThrow(path)
                    ?.process(childPath, _element, argument)
                    ?.also { _element = it }
            }
        }
        return _element
    }

    private fun JsonObject.retrieveAllRef(
        url: String,
        type: String,
    ): List<JsonObject>? {
        onScope(IdSchema::Scope).source ?: return null
        var currentEntry = this
        val entries = mutableListOf(currentEntry)
        do {
            val idRef = currentEntry.onScope(IdSchema::Scope).source
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
            JsonNull.withScope(::SchemaScope).apply {
                for (entry in this@merge.asReversed()) {
                    merge(entry)
                }
            }.collect()
        }
    }

    private fun SchemaScope.merge(next: JsonObject) {
        for ((key, nextChild) in next) {
            val previousChild = this[key]
            this[key] = when {
                previousChild is JsonObject && nextChild is JsonObject -> {
                    previousChild.withScope(::SchemaScope).apply {
                        when {
                            key == IdSchema.root -> mergeId(nextChild)
                            else -> merge(nextChild)
                        }
                    }.collect()
                }

                else -> nextChild
            }
        }
    }

    private fun SchemaScope.mergeId(next: JsonObject) {
        return next.withScope(IdSchema::Scope).apply {
            remove(IdSchema.Key.source) // we remove source, since previous is the source about to be merged
            if (idAutoGenerated == true && this@mergeId.withScope(IdSchema::Scope).idAutoGenerated != true) {
                // next id is auto-generated, we removed it since it weak against previous id not auto-generated
                remove(IdSchema.Key.value)
                remove(IdSchema.Key.id_auto_generated)
            }
        }.collect().let { merge(it) }
    }

    private fun JsonObject.udpateSetting(element: JsonElement) =
        if (onScope(IdSchema::Scope).source != null) {
            element.onScope(SettingSchema::Scope)
                .takeIf { it.missingDefinition != true }
                ?.apply { missingDefinition = true }
                ?.collect()
                ?.let { setting ->
                    element.replaceOrInsert(SettingSchema.root.toPath(), setting)
                }
        } else null
}