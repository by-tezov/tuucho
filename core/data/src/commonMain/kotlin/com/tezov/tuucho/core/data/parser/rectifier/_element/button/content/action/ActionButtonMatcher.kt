package com.tezov.tuucho.core.data.parser.rectifier._element.button.content.action


import com.tezov.tuucho.core.data.parser._system.isSubsetOf
import com.tezov.tuucho.core.data.parser._system.isTypeOf
import com.tezov.tuucho.core.data.parser._system.lastSegmentIs
import com.tezov.tuucho.core.data.parser.rectifier._system.MatcherRectifierProtocol
import com.tezov.tuucho.core.domain._system.JsonElementPath
import com.tezov.tuucho.core.domain._system.find
import com.tezov.tuucho.core.domain.model.schema.material.TypeSchema
import com.tezov.tuucho.core.domain.model.schema.material._element.ButtonSchema
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

class ActionButtonMatcher : MatcherRectifierProtocol {

    override fun accept(path: JsonElementPath, element: JsonElement): Boolean {
        if (!path.lastSegmentIs(ButtonSchema.Content.Key.action)) return false
        val parent = element.find(path.parent()) as? JsonObject
        return parent.isSubsetOf(ButtonSchema.Component.Value.subset)
                && parent.isTypeOf(TypeSchema.Value.content)
    }

}