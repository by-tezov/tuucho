package com.tezov.tuucho.core.data.parser.rectifier._element.label.content


import com.tezov.tuucho.core.data.parser._system.isSubsetOf
import com.tezov.tuucho.core.data.parser._system.isTypeOf
import com.tezov.tuucho.core.data.parser._system.lastSegmentIs
import com.tezov.tuucho.core.data.parser.rectifier._system.MatcherRectifierProtocol
import com.tezov.tuucho.core.domain.business._system.JsonElementPath
import com.tezov.tuucho.core.domain.business._system.find
import com.tezov.tuucho.core.domain.business.model.schema.material.TypeSchema
import com.tezov.tuucho.core.domain.business.model.schema.material._element.LabelSchema
import kotlinx.serialization.json.JsonElement

class ContentLabelTextMatcher : MatcherRectifierProtocol {

    override fun accept(path: JsonElementPath, element: JsonElement): Boolean {
        if (!path.lastSegmentIs(LabelSchema.Content.Key.value)) return false
        val parent = element.find(path.parent())
        return parent.isSubsetOf(LabelSchema.Component.Value.subset)
                && parent.isTypeOf(TypeSchema.Value.content)
    }

}