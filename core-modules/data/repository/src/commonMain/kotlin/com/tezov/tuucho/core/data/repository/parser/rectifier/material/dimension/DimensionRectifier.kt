package com.tezov.tuucho.core.data.repository.parser.rectifier.material.dimension

import com.tezov.tuucho.core.data.repository.parser.rectifier.material._system.AbstractRectifier
import com.tezov.tuucho.core.data.repository.parser.rectifier.material._system.RectifierHelper.rectifyIds
import com.tezov.tuucho.core.data.repository.parser.rectifier.material._system.RectifierMatcherProtocol
import com.tezov.tuucho.core.data.repository.parser.rectifier.material._system.RectifierProtocol
import com.tezov.tuucho.core.domain.business._system.koin.AssociateDSL.getAllAssociated
import com.tezov.tuucho.core.domain.business.jsonSchema._system.SymbolData
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.DimensionSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.IdSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import com.tezov.tuucho.core.domain.tool.json.find
import com.tezov.tuucho.core.domain.tool.json.string
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import org.koin.core.scope.Scope

class DimensionRectifier(
    scope: Scope
) : AbstractRectifier(scope) {
    sealed class Association {
        object Matcher : Association()

        object Processor : Association()
    }

    override val key = DimensionSchema.root

    override val matchers: List<RectifierMatcherProtocol> by lazy {
        scope.getAllAssociated(Association.Matcher::class)
    }
    override val childProcessors: List<RectifierProtocol> by lazy {
        scope.getAllAssociated(Association.Processor::class)
    }

    override fun beforeAlterPrimitive(
        path: JsonElementPath,
        element: JsonElement,
    ) = element
        .find(path)
        .withScope(DimensionSchema::Scope)
        .apply {
            type = TypeSchema.Value.dimension
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
        element: JsonElement,
    ) = element
        .find(path)
        .withScope(DimensionSchema::Scope)
        .apply {
            type = TypeSchema.Value.dimension
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
            .withScope(DimensionSchema::Scope)
            .takeIf {
                it
                    .rectifyIds(DimensionSchema.Value.Group.common)
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
