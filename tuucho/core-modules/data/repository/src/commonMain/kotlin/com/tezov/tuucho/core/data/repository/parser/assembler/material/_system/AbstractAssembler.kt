@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.data.repository.parser.assembler.material._system

import com.tezov.tuucho.core.data.repository.exception.DataException
import com.tezov.tuucho.core.domain.business._system.koin.TuuchoKoinScopeComponent
import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import com.tezov.tuucho.core.domain.tool.json.ROOT_PATH
import com.tezov.tuucho.core.domain.tool.json.find
import com.tezov.tuucho.core.domain.tool.json.replaceOrInsert
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import org.koin.core.component.inject
import org.koin.core.scope.Scope

abstract class AbstractAssembler(
    private val _scope: Scope? = null
) : AssemblerProtocol,
    TuuchoKoinScopeComponent {
    override val lazyScope: Lazy<Scope> = lazy {
        _scope ?: throw DataException.Default("scope can't be null, either pass it in the constructor or override it")
    }

    private val jsonObjectMerger: JsonObjectMerger by inject()

    protected open val matchers: List<AssemblerMatcherProtocol> = emptyList()
    protected open val childProcessors: List<AssemblerProtocol> = emptyList()

    private fun <T> List<T>.singleOrThrow(
        path: JsonElementPath
    ): T? {
        if (size > 1) throw DataException.Default("Only one child processor can accept the element at path $path")
        return firstOrNull()
    }

    override fun accept(
        path: JsonElementPath,
        element: JsonElement,
    ) = matchers.any { it.accept(path, element) }

    override suspend fun process(
        context: AssemblerProtocol.Context,
        path: JsonElementPath,
        element: JsonElement
    ): JsonElement = with(element.find(path)) {
        when (this) {
            is JsonArray -> processArray(context, path, element)
            is JsonObject -> processObject(context, path, element)
            is JsonPrimitive -> throw DataException.Default("by design can't assemble primitive")
        }
    }

    private suspend fun JsonArray.processArray(
        context: AssemblerProtocol.Context,
        path: JsonElementPath,
        element: JsonElement
    ) = element.replaceOrInsert(
        path,
        map {
            (it as? JsonObject)
                ?: throw DataException.Default("by design element inside array must be an object")
            it.processObject(context, ROOT_PATH, it)
        }.let(::JsonArray)
    )

    private suspend fun JsonObject.processObject(
        context: AssemblerProtocol.Context,
        path: JsonElementPath,
        element: JsonElement
    ): JsonElement {
        var _element = element
        val current: JsonObject = context.findAllRefOrNullFetcher
            .invoke(this, schemaType)
            ?.let { it.rectify(context, path, element) ?: it }
            ?.let { jsonObjectMerger.merge(it) }
            ?.also { _element = element.replaceOrInsert(path, it) }
            ?: run {
                // fallback, nothing to resolved
                rectify(context, path, element)
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
                    ?.process(context, childPath, _element)
                    ?.also { _element = it }
            }
        }
        return _element
    }

    protected open fun List<JsonObject>.rectify(
        context: AssemblerProtocol.Context,
        path: JsonElementPath,
        element: JsonElement
    ): List<JsonObject>? = null

    protected open fun JsonObject.rectify(
        context: AssemblerProtocol.Context,
        path: JsonElementPath,
        element: JsonElement
    ): JsonObject? = null
}
