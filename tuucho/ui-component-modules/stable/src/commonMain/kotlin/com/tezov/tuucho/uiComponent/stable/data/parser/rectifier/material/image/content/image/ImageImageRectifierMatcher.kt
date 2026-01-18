package com.tezov.tuucho.uiComponent.stable.data.parser.rectifier.material.image.content.image

import com.tezov.tuucho.core.data.repository.parser._system.isSubsetOf
import com.tezov.tuucho.core.data.repository.parser._system.isTypeOf
import com.tezov.tuucho.core.data.repository.parser._system.lastSegmentIs
import com.tezov.tuucho.core.data.repository.parser.rectifier.material._system.RectifierMatcherProtocol
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import com.tezov.tuucho.core.domain.tool.json.find
import com.tezov.tuucho.uiComponent.stable.domain.jsonSchema.material.ImageSchema
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

class ImageImageRectifierMatcher : RectifierMatcherProtocol {
    override fun accept(
        path: JsonElementPath,
        element: JsonElement
    ): Boolean {
        if (!path.lastSegmentIs(ImageSchema.Content.Key.value)) return false
        val parent = element.find(path.parent()) as? JsonObject
        return parent.isSubsetOf(ImageSchema.Component.Value.subset) &&
            parent.isTypeOf(TypeSchema.Value.content)
    }
}
