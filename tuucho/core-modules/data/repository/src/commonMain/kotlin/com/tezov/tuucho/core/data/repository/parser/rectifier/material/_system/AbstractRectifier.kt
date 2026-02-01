@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.data.repository.parser.rectifier.material._system

import com.tezov.tuucho.core.data.repository.exception.DataException
import com.tezov.tuucho.core.domain.business._system.koin.TuuchoKoinScopeComponent
import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import com.tezov.tuucho.core.domain.tool.json.find
import com.tezov.tuucho.core.domain.tool.json.replaceOrInsert
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import org.koin.core.scope.Scope

// IMPROVE add meta data 'path' for breaker, assembler and shadower to improve speed
abstract class AbstractRectifier(
    private val _scope: Scope? = null
) : RectifierProtocol,
    TuuchoKoinScopeComponent {
    override val lazyScope: Lazy<Scope> = lazy {
        _scope ?: throw DataException.Default("scope can't be null, either pass it in the constructor or override it")
    }

    protected open val matchers: List<RectifierMatcherProtocol> = emptyList()
    protected open val childProcessors: List<RectifierProtocol> = emptyList()

    override fun accept(
        path: JsonElementPath,
        element: JsonElement
    ) = matchers.any { it.accept(path, element) }

    override fun process(
        context: RectifierProtocol.Context,
        path: JsonElementPath,
        element: JsonElement
    ): JsonElement {
        var _element = element
        beforeAlter(context, path, _element)?.let {
            _element = _element.replaceOrInsert(path, it)
        }
        if (childProcessors.isNotEmpty()) {
            _element = alter(context, path, _element)
        }
        afterAlter(context, path, _element)?.let {
            _element = _element.replaceOrInsert(path, it)
        }
        return _element
    }

    private fun beforeAlter(
        context: RectifierProtocol.Context,
        path: JsonElementPath,
        element: JsonElement
    ) = with(element.find(path)) {
        when (this) {
            is JsonNull -> beforeAlterNull(context, path, element)
            is JsonPrimitive -> beforeAlterPrimitive(context, path, element)
            is JsonObject -> beforeAlterObject(context, path, element)
            is JsonArray -> beforeAlterArray(context, path, element)
        }
    }

    protected open fun beforeAlterNull(
        context: RectifierProtocol.Context,
        path: JsonElementPath,
        element: JsonElement
    ): JsonElement? = null

    protected open fun beforeAlterPrimitive(
        context: RectifierProtocol.Context,
        path: JsonElementPath,
        element: JsonElement
    ): JsonElement? = null

    protected open fun beforeAlterObject(
        context: RectifierProtocol.Context,
        path: JsonElementPath,
        element: JsonElement
    ): JsonElement? = null

    protected open fun beforeAlterArray(
        context: RectifierProtocol.Context,
        path: JsonElementPath,
        element: JsonElement
    ): JsonElement? = null

    private fun alter(
        context: RectifierProtocol.Context,
        path: JsonElementPath,
        element: JsonElement
    ) = with(element.find(path)) {
        var _element = element
        when (this) {
            is JsonPrimitive, is JsonArray -> {
                childProcessors
                    .asSequence()
                    .filter { it.accept(path, _element) }
                    .forEach { _element = it.process(context, path, _element) }
            }

            is JsonObject -> {
                this.jsonObject.keys.forEach { childKey ->
                    val childPath = path.child(childKey)
                    childProcessors
                        .asSequence()
                        .filter { it.accept(childPath, _element) }
                        .forEach { _element = it.process(context, childPath, _element) }
                }
            }
        }
        _element
    }

    private fun afterAlter(
        context: RectifierProtocol.Context,
        path: JsonElementPath,
        element: JsonElement
    ) = with(element.find(path)) {
        when (this) {
            is JsonNull -> afterAlterNull(context, path, element)
            is JsonPrimitive -> afterAlterPrimitive(context, path, element)
            is JsonObject -> afterAlterObject(context, path, element)
            is JsonArray -> afterAlterArray(context, path, element)
        }
    }

    protected open fun afterAlterNull(
        context: RectifierProtocol.Context,
        path: JsonElementPath,
        element: JsonElement
    ): JsonElement? = null

    protected open fun afterAlterPrimitive(
        context: RectifierProtocol.Context,
        path: JsonElementPath,
        element: JsonElement
    ): JsonElement? = null

    protected open fun afterAlterObject(
        context: RectifierProtocol.Context,
        path: JsonElementPath,
        element: JsonElement
    ): JsonElement? = null

    protected open fun afterAlterArray(
        context: RectifierProtocol.Context,
        path: JsonElementPath,
        element: JsonElement
    ): JsonElement? = null
}
