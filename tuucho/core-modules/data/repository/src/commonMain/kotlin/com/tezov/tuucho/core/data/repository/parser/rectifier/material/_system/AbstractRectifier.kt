@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.data.repository.parser.rectifier.material._system

import com.tezov.tuucho.core.domain.business._system.koin.TuuchoKoinScopeComponent
import com.tezov.tuucho.core.domain.business.exception.DomainException
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
    override val scope: Scope
        get() = _scope ?: throw DomainException.Default("scope can't be null, either pass it in the constructor or override it")

    protected open val matchers: List<RectifierMatcherProtocol> = emptyList()
    protected open val childProcessors: List<RectifierProtocol> = emptyList()

    override fun accept(
        path: JsonElementPath,
        element: JsonElement
    ) = matchers.any { it.accept(path, element) }

    override fun process(
        path: JsonElementPath,
        element: JsonElement
    ): JsonElement {
        var _element = element
        beforeAlter(path, _element)?.let {
            _element = _element.replaceOrInsert(path, it)
        }
        if (childProcessors.isNotEmpty()) {
            _element = alter(path, _element)
        }
        afterAlter(path, _element)?.let {
            _element = _element.replaceOrInsert(path, it)
        }
        return _element
    }

    private fun beforeAlter(
        path: JsonElementPath,
        element: JsonElement
    ) = with(element.find(path)) {
        when (this) {
            is JsonNull -> beforeAlterNull(path, element)
            is JsonPrimitive -> beforeAlterPrimitive(path, element)
            is JsonObject -> beforeAlterObject(path, element)
            is JsonArray -> beforeAlterArray(path, element)
        }
    }

    protected open fun beforeAlterNull(
        path: JsonElementPath,
        element: JsonElement
    ): JsonElement? = null

    protected open fun beforeAlterPrimitive(
        path: JsonElementPath,
        element: JsonElement
    ): JsonElement? = null

    protected open fun beforeAlterObject(
        path: JsonElementPath,
        element: JsonElement
    ): JsonElement? = null

    protected open fun beforeAlterArray(
        path: JsonElementPath,
        element: JsonElement
    ): JsonElement? = null

    private fun alter(
        path: JsonElementPath,
        element: JsonElement
    ) = with(element.find(path)) {
        var _element = element
        when (this) {
            is JsonPrimitive, is JsonArray -> {
                childProcessors
                    .asSequence()
                    .filter { it.accept(path, _element) }
                    .forEach { _element = it.process(path, _element) }
            }

            is JsonObject -> {
                this.jsonObject.keys.forEach { childKey ->
                    val childPath = path.child(childKey)
                    childProcessors
                        .asSequence()
                        .filter { it.accept(childPath, _element) }
                        .forEach { _element = it.process(childPath, _element) }
                }
            }
        }
        _element
    }

    private fun afterAlter(
        path: JsonElementPath,
        element: JsonElement
    ) = with(element.find(path)) {
        when (this) {
            is JsonNull -> afterAlterNull(path, element)
            is JsonPrimitive -> afterAlterPrimitive(path, element)
            is JsonObject -> afterAlterObject(path, element)
            is JsonArray -> afterAlterArray(path, element)
        }
    }

    protected open fun afterAlterNull(
        path: JsonElementPath,
        element: JsonElement
    ): JsonElement? = null

    protected open fun afterAlterPrimitive(
        path: JsonElementPath,
        element: JsonElement
    ): JsonElement? = null

    protected open fun afterAlterObject(
        path: JsonElementPath,
        element: JsonElement
    ): JsonElement? = null

    protected open fun afterAlterArray(
        path: JsonElementPath,
        element: JsonElement
    ): JsonElement? = null
}
