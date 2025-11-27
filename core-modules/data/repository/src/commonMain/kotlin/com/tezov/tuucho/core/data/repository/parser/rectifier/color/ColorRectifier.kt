package com.tezov.tuucho.core.data.repository.parser.rectifier.color

import com.tezov.tuucho.core.data.repository.di.MaterialRectifierModule.Name
import com.tezov.tuucho.core.data.repository.parser.rectifier._system.AbstractRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier._system.MatcherRectifierProtocol
import com.tezov.tuucho.core.data.repository.parser.rectifier._system.RectifierHelper.rectifyIds
import com.tezov.tuucho.core.domain.business.jsonSchema._system.SymbolData
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.ColorSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.IdSchema
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
class ColorRectifier : AbstractRectifier() {
    override val key = ColorSchema.root
    override val matchers: List<MatcherRectifierProtocol> by inject(
        Name.Matcher.COLOR
    )
    override val childProcessors: List<AbstractRectifier> by inject(
        Name.Processor.COLOR
    )

    override fun beforeAlterPrimitive(
        path: JsonElementPath,
        element: JsonElement
    ) = element
        .find(path)
        .withScope(ColorSchema::Scope)
        .apply {
            type = TypeSchema.Value.color
            val value = this.element.string
            if (value.startsWith(SymbolData.ID_REF_INDICATOR)) {
                id = JsonPrimitive(value)
            } else {
                id = JsonNull
                default = value
            }
        }.collect()

    override fun beforeAlterObject(
        path: JsonElementPath,
        element: JsonElement
    ) = element
        .find(path)
        .withScope(ColorSchema::Scope)
        .apply {
            type = TypeSchema.Value.color
            id ?: run { id = JsonNull }
        }.collect()

    override fun afterAlterObject(
        path: JsonElementPath,
        element: JsonElement,
    ): JsonElement? {
        var valueRectified: String?
        var sourceRectified: String?
        return element
            .find(path)
            .withScope(ColorSchema::Scope)
            .takeIf {
                it
                    .rectifyIds(ColorSchema.Value.Group.common)
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
