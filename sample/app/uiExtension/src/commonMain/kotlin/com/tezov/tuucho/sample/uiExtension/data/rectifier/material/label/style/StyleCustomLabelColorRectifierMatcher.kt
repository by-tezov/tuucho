package com.tezov.tuucho.sample.uiExtension.data.rectifier.material.label.style

import com.tezov.tuucho.core.data.repository.parser._system.isSubsetOf
import com.tezov.tuucho.core.data.repository.parser._system.isTypeOf
import com.tezov.tuucho.core.data.repository.parser._system.lastSegmentIsAny
import com.tezov.tuucho.core.data.repository.parser.rectifier.material._system.RectifierMatcherProtocol
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import com.tezov.tuucho.core.domain.tool.json.find
import com.tezov.tuucho.sample.uiExtension.domain.CustomLabelSchema.Component
import com.tezov.tuucho.sample.uiExtension.domain.CustomLabelSchema.Style
import kotlinx.serialization.json.JsonElement

class StyleCustomLabelColorRectifierMatcher : RectifierMatcherProtocol {

    private val segments = listOf(
        Style.Key.fontColorLight,
        Style.Key.fontColorDark
    )

    override fun accept(
        path: JsonElementPath,
        element: JsonElement
    ): Boolean {
        if (!path.lastSegmentIsAny(segments)) return false
        val parent = element.find(path.parent())
        return parent.isTypeOf(TypeSchema.Value.style) &&
                parent.isSubsetOf(Component.Value.subset)
    }
}
