package com.tezov.tuucho.core.data.parser.rectifier._element.layout.linear

import com.tezov.tuucho.core.data.parser._system.MatcherProtocol
import com.tezov.tuucho.core.data.parser._system.isSubsetOf
import com.tezov.tuucho.core.data.parser._system.isTypeOf
import com.tezov.tuucho.core.data.parser._system.lastSegmentIs
import com.tezov.tuucho.core.domain._system.JsonElementPath
import com.tezov.tuucho.core.domain._system.find
import com.tezov.tuucho.core.domain.model.schema.material.TypeSchema
import com.tezov.tuucho.core.domain.model.schema.material._element.LayoutLinearSchema
import kotlinx.serialization.json.JsonElement

class ContentLayoutLinearItemsMatcher : MatcherProtocol {

    override fun accept(path: JsonElementPath, element: JsonElement): Boolean {
        if (!path.lastSegmentIs(LayoutLinearSchema.Content.Key.items)) return false
        val parent = element.find(path.parent())
        return parent.isSubsetOf(LayoutLinearSchema.Component.Value.subset)
                && parent.isTypeOf(TypeSchema.Value.content)
    }

}