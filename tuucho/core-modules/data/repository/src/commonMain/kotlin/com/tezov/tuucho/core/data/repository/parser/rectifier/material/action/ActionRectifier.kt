package com.tezov.tuucho.core.data.repository.parser.rectifier.material.action

import com.tezov.tuucho.core.data.repository.parser.rectifier.material._system.AbstractRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.material._system.RectifierHelper.rectifyIds
import com.tezov.tuucho.core.data.repository.parser.rectifier.material._system.RectifierMatcherProtocol
import com.tezov.tuucho.core.data.repository.parser.rectifier.material._system.RectifierProtocol
import com.tezov.tuucho.core.domain.business._system.koin.Associate.getAllAssociated
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.IdSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.IdSchema.isRef
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.action.ActionSchema
import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import com.tezov.tuucho.core.domain.tool.json.find
import com.tezov.tuucho.core.domain.tool.json.string
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonArray
import org.koin.core.scope.Scope

class ActionRectifier(
    scope: Scope
) : AbstractRectifier(scope) {
    sealed class Association {
        object Matcher : Association()

        object Processor : Association()
    }

    override val key = ActionSchema.root
    override val matchers: List<RectifierMatcherProtocol> by lazy {
        scope.getAllAssociated(Association.Matcher::class)
    }
    override val childProcessors: List<RectifierProtocol> by lazy {
        scope.getAllAssociated(Association.Processor::class)
    }

    override fun beforeAlterPrimitive(
        context: RectifierProtocol.Context,
        path: JsonElementPath,
        element: JsonElement,
    ) = element
        .find(path)
        .withScope(ActionSchema::Scope)
        .apply {
            type = TypeSchema.Value.action
            val value = this.element.string
            if (value.isRef) {
                id = this.element
            } else {
                id = JsonNull
                primaries = listOf(this.element).let(::JsonArray)
            }
        }.collect()

    override fun beforeAlterArray(
        context: RectifierProtocol.Context,
        path: JsonElementPath,
        element: JsonElement,
    ) = element
        .find(path)
        .withScope(ActionSchema::Scope)
        .apply {
            type = TypeSchema.Value.action
            id = JsonNull
            primaries = this.element.jsonArray
        }.collect()

    override fun beforeAlterObject(
        context: RectifierProtocol.Context,
        path: JsonElementPath,
        element: JsonElement,
    ) = element
        .find(path)
        .withScope(ActionSchema::Scope)
        .apply {
            type = TypeSchema.Value.action
            id ?: run { id = JsonNull }
            val ignoreKeys = listOf(IdSchema.root, TypeSchema.root)
            keys()
                .asSequence()
                .filter { !ignoreKeys.contains(it) }
                .forEach { key ->
                    get(key)
                        ?.takeIf {
                            it is JsonPrimitive
                        }?.let {
                            set(key, listOf(it).let(::JsonArray))
                        }
                }
        }.collect()

    override fun afterAlterObject(
        context: RectifierProtocol.Context,
        path: JsonElementPath,
        element: JsonElement,
    ): JsonElement? {
        var valueRectified: String?
        var sourceRectified: String?
        return element
            .find(path)
            .withScope(ActionSchema::Scope)
            .takeIf {
                it
                    .rectifyIds(ActionSchema.Value.Group.common)
                    .also { (value, source) ->
                        valueRectified = value
                        sourceRectified = source
                    }
                valueRectified != null || sourceRectified != null
            }?.apply {
                id = onScope(IdSchema::Scope)
                    .apply {
                        valueRectified?.let { value = it }
                        sourceRectified?.let { source = it }
                    }.collect()
            }?.collect()
    }
}
