package com.tezov.tuucho.core.data.parser.rectifier

import com.tezov.tuucho.core.data.parser.rectifier._system.MatcherRectifierProtocol
import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import com.tezov.tuucho.core.domain.tool.json.find
import com.tezov.tuucho.core.domain.tool.json.replaceOrInsert
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import org.koin.core.component.KoinComponent

//Improve add meta data 'path' for breaker, assembler and shadower to improve speed
abstract class AbstractRectifier : MatcherRectifierProtocol, KoinComponent {
    protected open val matchers: List<MatcherRectifierProtocol> = emptyList()
    protected open val childProcessors: List<AbstractRectifier> = emptyList()

    override fun accept(
        path: JsonElementPath,
        element: JsonElement
    ) = matchers.any { it.accept(path, element) }

    fun process(
        path: JsonElementPath, element: JsonElement
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

    private fun beforeAlter(path: JsonElementPath, element: JsonElement) =
        with(element.find(path)) {
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
    ): JsonElement? {
        return null
    }

    protected open fun beforeAlterPrimitive(
        path: JsonElementPath,
        element: JsonElement
    ): JsonElement? {
        return null
    }

    protected open fun beforeAlterObject(
        path: JsonElementPath,
        element: JsonElement
    ): JsonElement? {
        return null
    }

    protected open fun beforeAlterArray(
        path: JsonElementPath,
        element: JsonElement
    ): JsonElement? {
        return null
    }

    private fun alter(path: JsonElementPath, element: JsonElement) = with(element.find(path)) {
        var _element = element
        when (this) {
            is JsonPrimitive, is JsonArray -> {
                childProcessors.asSequence()
                    .filter { it.accept(path, _element) }
                    .forEach { _element = it.process(path, _element) }
            }

            is JsonObject -> {
                this.jsonObject.keys.forEach { childKey ->
                    val childPath = path.child(childKey)
                    childProcessors.asSequence()
                        .filter { it.accept(childPath, _element) }
                        .forEach { _element = it.process(childPath, _element) }
                }
            }
        }
        _element
    }

    private fun afterAlter(path: JsonElementPath, element: JsonElement) = with(element.find(path)) {
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
    ): JsonElement? {
        return null
    }

    protected open fun afterAlterPrimitive(
        path: JsonElementPath,
        element: JsonElement
    ): JsonElement? {
        return null
    }

    protected open fun afterAlterObject(
        path: JsonElementPath,
        element: JsonElement
    ): JsonElement? {
        return null
    }

    protected open fun afterAlterArray(
        path: JsonElementPath,
        element: JsonElement
    ): JsonElement? {
        return null
    }
}