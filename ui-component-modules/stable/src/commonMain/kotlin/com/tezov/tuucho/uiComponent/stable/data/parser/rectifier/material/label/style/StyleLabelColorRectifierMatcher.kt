package com.tezov.tuucho.uiComponent.stable.data.parser.rectifier.material.label.style

import com.tezov.tuucho.core.data.repository.parser._system.isSubsetOf
import com.tezov.tuucho.core.data.repository.parser._system.isTypeOf
import com.tezov.tuucho.core.data.repository.parser._system.lastSegmentIs
import com.tezov.tuucho.core.data.repository.parser.rectifier.material._system.RectifierMatcherProtocol
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import com.tezov.tuucho.core.domain.tool.json.find
import com.tezov.tuucho.uiComponent.stable.domain.jsonSchema.material.LabelSchema
import kotlinx.serialization.json.JsonElement

class StyleLabelColorRectifierMatcher : RectifierMatcherProtocol {
    override fun accept(
        path: JsonElementPath,
        element: JsonElement
    ): Boolean {
        if (!path.lastSegmentIs(LabelSchema.Style.Key.fontColor)) return false
        val parent = element.find(path.parent())
        return parent.isTypeOf(TypeSchema.Value.style) &&
            parent.isSubsetOf(LabelSchema.Component.Value.subset)
    }
}
