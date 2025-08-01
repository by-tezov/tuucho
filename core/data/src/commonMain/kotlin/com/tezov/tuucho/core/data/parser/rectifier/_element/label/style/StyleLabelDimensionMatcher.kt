package com.tezov.tuucho.core.data.parser.rectifier._element.label.style


import com.tezov.tuucho.core.data.parser._system.isSubsetOf
import com.tezov.tuucho.core.data.parser._system.isTypeOf
import com.tezov.tuucho.core.data.parser._system.lastSegmentIs
import com.tezov.tuucho.core.data.parser.rectifier._system.MatcherRectifierProtocol
import com.tezov.tuucho.core.domain._system.JsonElementPath
import com.tezov.tuucho.core.domain._system.find
import com.tezov.tuucho.core.domain.model.schema.material.TypeSchema
import com.tezov.tuucho.core.domain.model.schema.material._element.LabelSchema
import kotlinx.serialization.json.JsonElement

class StyleLabelDimensionMatcher : MatcherRectifierProtocol {

    override fun accept(path: JsonElementPath, element: JsonElement): Boolean {
        if (!path.lastSegmentIs(LabelSchema.Style.Key.fontSize)) return false
        val parent = element.find(path.parent())
        return parent.isTypeOf(TypeSchema.Value.style)
                && parent.isSubsetOf(LabelSchema.Component.Value.subset)
    }

}