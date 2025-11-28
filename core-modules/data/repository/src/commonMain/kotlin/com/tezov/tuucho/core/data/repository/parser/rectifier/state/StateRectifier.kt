package com.tezov.tuucho.core.data.repository.parser.rectifier.state

import com.tezov.tuucho.core.data.repository.di.MaterialRectifierModule
import com.tezov.tuucho.core.data.repository.parser._system.lastSegmentIs
import com.tezov.tuucho.core.data.repository.parser._system.parentIsTypeOf
import com.tezov.tuucho.core.data.repository.parser.rectifier._system.AbstractRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier._system.RectifierHelper.rectifyIds
import com.tezov.tuucho.core.data.repository.parser.rectifier._system.RectifierHelper.retrieveSubsetOrMarkUnknown
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.IdSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.IdSchema.requireIsRef
import com.tezov.tuucho.core.domain.business.jsonSchema.material.StateSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.domain.tool.annotation.TuuchoExperimentalAPI
import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import com.tezov.tuucho.core.domain.tool.json.find
import com.tezov.tuucho.core.domain.tool.json.string
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import org.koin.core.component.inject

@OptIn(TuuchoExperimentalAPI::class)
class StateRectifier : AbstractRectifier() {
    override val key = StateSchema.root

    override val childProcessors: List<AbstractRectifier> by inject(
        MaterialRectifierModule.Name.Processor.STATE
    )

    override fun accept(
        path: JsonElementPath,
        element: JsonElement
    ) = (path.lastSegmentIs(TypeSchema.Value.state) &&
        path.parentIsTypeOf(
            element,
            TypeSchema.Value.component
        )) ||
        super.accept(path, element)

    override fun beforeAlterPrimitive(
        path: JsonElementPath,
        element: JsonElement,
    ) = element
        .find(path)
        .withScope(StateSchema::Scope)
        .apply {
            type = TypeSchema.Value.state
            val value = this.element.string.requireIsRef()
            id = JsonPrimitive(value)
        }.collect()

    override fun beforeAlterObject(
        path: JsonElementPath,
        element: JsonElement
    ) = element
        .find(path)
        .withScope(StateSchema::Scope)
        .apply {
            type = TypeSchema.Value.state
            id ?: run { id = JsonNull }
            subset = retrieveSubsetOrMarkUnknown(path, element)
        }.collect()

    override fun afterAlterObject(
        path: JsonElementPath,
        element: JsonElement,
    ): JsonElement? {
        var valueRectified: String?
        var sourceRectified: String?
        return element
            .find(path)
            .withScope(StateSchema::Scope)
            .takeIf {
                it
                    .rectifyIds(StateSchema.Value.Group.common)
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
