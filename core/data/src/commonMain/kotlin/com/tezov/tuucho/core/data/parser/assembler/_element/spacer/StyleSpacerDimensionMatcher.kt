package com.tezov.tuucho.core.data.parser.assembler._element.spacer

import com.tezov.tuucho.core.data.parser._system.MatcherProtocol
import com.tezov.tuucho.core.data.parser._system.isSubsetOf
import com.tezov.tuucho.core.data.parser._system.isTypeOf
import com.tezov.tuucho.core.data.parser._system.lastSegmentIsAny
import com.tezov.tuucho.core.domain._system.JsonElementPath
import com.tezov.tuucho.core.domain._system.find
import com.tezov.tuucho.core.domain.model.schema.material.StyleSchema
import com.tezov.tuucho.core.domain.model.schema.material.TypeSchema
import com.tezov.tuucho.core.domain.model.schema.material._element.SpacerSchema
import kotlinx.serialization.json.JsonElement

class StyleSpacerDimensionMatcher : MatcherProtocol {

    private val segments = listOf(
        SpacerSchema.Style.Key.weight,
        StyleSchema.Key.width,
        StyleSchema.Key.height
    )

    override fun accept(path: JsonElementPath, element: JsonElement): Boolean {
        if (!path.lastSegmentIsAny(segments)) return false
        val parent = element.find(path.parent())
        return parent.isTypeOf(TypeSchema.Value.style)
                && parent.isSubsetOf(SpacerSchema.Component.Value.subset)
    }

}