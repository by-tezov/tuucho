package com.tezov.tuucho.core.data.repository.parser.assembler

import com.tezov.tuucho.core.data.repository.exception.DataException
import com.tezov.tuucho.core.data.repository.parser.assembler._system.FindAllRefOrNullFetcherProtocol
import com.tezov.tuucho.core.data.repository.parser.assembler._system.JsonObjectMerger
import com.tezov.tuucho.core.data.repository.parser.assembler._system.MatcherAssemblerProtocol
import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import com.tezov.tuucho.core.domain.tool.json.find
import com.tezov.tuucho.core.domain.tool.json.replaceOrInsert
import com.tezov.tuucho.core.domain.tool.json.toPath
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

abstract class AbstractAssembler : MatcherAssemblerProtocol, KoinComponent {
    abstract val schemaType: String

    private val jsonObjectMerger: JsonObjectMerger by inject()

    protected open val matchers: List<MatcherAssemblerProtocol> = emptyList()
    protected open val childProcessors: List<AbstractAssembler> = emptyList()

    private fun <T> List<T>.singleOrThrow(path: JsonElementPath): T? {
        if (size > 1) throw DataException.Default("Only one child processor can accept the element at path $path")
        return firstOrNull()
    }

    override fun accept(
        path: JsonElementPath,
        element: JsonElement,
    ) = matchers.any { it.accept(path, element) }

    suspend fun process(
        path: JsonElementPath,
        element: JsonElement,
        findAllRefOrNullFetcher: FindAllRefOrNullFetcherProtocol
    ): JsonElement = with(element.find(path)) {
        when (this) {
            is JsonArray -> processArray(path, element, findAllRefOrNullFetcher)
            is JsonObject -> processObject(path, element, findAllRefOrNullFetcher)
            is JsonPrimitive -> throw DataException.Default("by design can't assemble primitive")
        }
    }

    private suspend fun JsonArray.processArray(
        path: JsonElementPath,
        element: JsonElement,
        findAllRefOrNullFetcher: FindAllRefOrNullFetcherProtocol
    ) = element.replaceOrInsert(
        path,
        map {
            (it as? JsonObject)
                ?: throw DataException.Default("by design element inside array must be object")
            it.processObject("".toPath(), it, findAllRefOrNullFetcher)
        }.let(::JsonArray)
    )

    private suspend fun JsonObject.processObject(
        path: JsonElementPath,
        element: JsonElement,
        findAllRefOrNullFetcher: FindAllRefOrNullFetcherProtocol
    ): JsonElement {
        var _element = element
        val current: JsonObject = findAllRefOrNullFetcher
            .invoke(this, schemaType)
            ?.let { it.rectify(path, element) ?: it }
            ?.let { jsonObjectMerger.merge(it) }
            ?.also { _element = element.replaceOrInsert(path, it) }
            ?: run {
                // fallback, nothing to resolved
                rectify(path, element)
                    ?.also { _element = element.replaceOrInsert(path, it) }
            } ?: run {
                // fallback, nothing to rectify
                this
            }

        if (childProcessors.isNotEmpty()) {
            current.keys.forEach { childKey ->
                val childPath = path.child(childKey)
                childProcessors
                    .filter { it.accept(childPath, _element) }
                    .singleOrThrow(path)
                    ?.process(childPath, _element, findAllRefOrNullFetcher)
                    ?.also { _element = it }
            }
        }
        return _element
    }

    protected open fun List<JsonObject>.rectify(
        path: JsonElementPath,
        element: JsonElement
    ): List<JsonObject>? = null

    protected open fun JsonObject.rectify(
        path: JsonElementPath,
        element: JsonElement
    ): JsonObject? = null
}