package com.tezov.tuucho.sample.uiExtension.data.rectifier.material.label.content

import com.tezov.tuucho.core.data.repository.parser._system.isSubsetOf
import com.tezov.tuucho.core.data.repository.parser._system.isTypeOf
import com.tezov.tuucho.core.data.repository.parser._system.lastSegmentIs
import com.tezov.tuucho.core.data.repository.parser.rectifier.material._system.RectifierMatcherProtocol
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import com.tezov.tuucho.core.domain.tool.json.find
import com.tezov.tuucho.sample.uiExtension.domain.CustomLabelSchema.Component
import com.tezov.tuucho.sample.uiExtension.domain.CustomLabelSchema.Content
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

class ActionCustomLabelRectifierMatcher : RectifierMatcherProtocol {

    override fun accept(
        path: JsonElementPath,
        element: JsonElement
    ): Boolean {
        if (!path.lastSegmentIs(Content.Key.action)) return false
        val parent = element.find(path.parent()) as? JsonObject
        return parent.isSubsetOf(Component.Value.subset) &&
                parent.isTypeOf(TypeSchema.Value.content)
    }
}
