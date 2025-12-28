@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.data.repository.parser.rectifier.material._element.spacer

import com.tezov.tuucho.core.data.repository.parser._system.isSubsetOf
import com.tezov.tuucho.core.data.repository.parser._system.isTypeOf
import com.tezov.tuucho.core.data.repository.parser._system.lastSegmentIsAny
import com.tezov.tuucho.core.data.repository.parser.rectifier.material._system.RectifierMatcherProtocol
import com.tezov.tuucho.core.domain.business.jsonSchema.material.StyleSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material._element.SpacerSchema
import com.tezov.tuucho.core.domain.tool.annotation.TuuchoExperimentalAPI
import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import com.tezov.tuucho.core.domain.tool.json.find
import kotlinx.serialization.json.JsonElement

@OptIn(TuuchoExperimentalAPI::class)
class StyleSpacerDimensionRectifierMatcher : RectifierMatcherProtocol {
    private val segments = listOf(
        SpacerSchema.Style.Key.weight,
        StyleSchema.Key.width,
        StyleSchema.Key.height
    )

    override fun accept(
        path: JsonElementPath,
        element: JsonElement
    ): Boolean {
        if (!path.lastSegmentIsAny(segments)) return false
        val parent = element.find(path.parent())
        return parent.isTypeOf(TypeSchema.Value.style) &&
            parent.isSubsetOf(SpacerSchema.Component.Value.subset)
    }
}
