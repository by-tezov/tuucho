package com.tezov.tuucho.core.data.repository.parser.rectifier.material.option

import com.tezov.tuucho.core.data.repository.di.RectifierModule.Material.Name
import com.tezov.tuucho.core.data.repository.parser._system.lastSegmentIs
import com.tezov.tuucho.core.data.repository.parser._system.parentIsTypeOf
import com.tezov.tuucho.core.data.repository.parser.rectifier.material._system.AbstractRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.material._system.RectifierHelper
import com.tezov.tuucho.core.data.repository.parser.rectifier.material._system.RectifierHelper.rectifyIds
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.IdSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.IdSchema.requireIsRef
import com.tezov.tuucho.core.domain.business.jsonSchema.material.OptionSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.domain.tool.annotation.TuuchoExperimentalAPI
import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import com.tezov.tuucho.core.domain.tool.json.find
import com.tezov.tuucho.core.domain.tool.json.string
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import org.koin.core.component.inject
import org.koin.core.scope.Scope

@OptIn(TuuchoExperimentalAPI::class)
class OptionRectifier(
    scope: Scope
) : AbstractRectifier(scope) {
    override val key = OptionSchema.root

    override val childProcessors: List<AbstractRectifier> by inject(
        Name.Processor.OPTION
    )

    override fun accept(
        path: JsonElementPath,
        element: JsonElement
    ) = (path.lastSegmentIs(TypeSchema.Value.option) &&
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
        .withScope(OptionSchema::Scope)
        .apply {
            type = TypeSchema.Value.option
            val value = this.element.string.requireIsRef()
            id = JsonPrimitive(value)
        }.collect()

    override fun beforeAlterObject(
        path: JsonElementPath,
        element: JsonElement
    ) = element
        .find(path)
        .withScope(OptionSchema::Scope)
        .apply {
            type = TypeSchema.Value.option
            id ?: run { id = JsonNull }
            subset = RectifierHelper.retrieveSubsetOrMarkUnknown(path, element)
        }.collect()

    override fun afterAlterObject(
        path: JsonElementPath,
        element: JsonElement,
    ): JsonElement? {
        var valueRectified: String?
        var sourceRectified: String?
        return element
            .find(path)
            .withScope(OptionSchema::Scope)
            .takeIf {
                it
                    .rectifyIds(OptionSchema.Value.Group.common)
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
