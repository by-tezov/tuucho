package com.tezov.tuucho.core.data.repository.parser.rectifier._element.layout.linear


import com.tezov.tuucho.core.data.repository.parser._system.isSubsetOf
import com.tezov.tuucho.core.data.repository.parser._system.isTypeOf
import com.tezov.tuucho.core.data.repository.parser._system.lastSegmentIs
import com.tezov.tuucho.core.data.repository.parser.rectifier._system.MatcherRectifierProtocol
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material._element.layout.LayoutLinearSchema
import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import com.tezov.tuucho.core.domain.tool.json.find
import kotlinx.serialization.json.JsonElement

class ContentLayoutLinearItemsMatcher : MatcherRectifierProtocol {

    override fun accept(path: JsonElementPath, element: JsonElement): Boolean {
        if (!path.lastSegmentIs(LayoutLinearSchema.Content.Key.items)) return false
        val parent = element.find(path.parent())
        return parent.isSubsetOf(LayoutLinearSchema.Component.Value.subset)
                && parent.isTypeOf(TypeSchema.Value.content)
    }

}