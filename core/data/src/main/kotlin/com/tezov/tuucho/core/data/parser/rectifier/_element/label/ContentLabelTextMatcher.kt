package com.tezov.tuucho.core.data.parser.rectifier._element.label

import com.tezov.tuucho.core.data.parser._system.Matcher
import com.tezov.tuucho.core.data.parser._system.isSubsetOf
import com.tezov.tuucho.core.data.parser._system.isTypeOf
import com.tezov.tuucho.core.data.parser._system.lastSegmentIs
import com.tezov.tuucho.core.domain._system.JsonElementPath
import com.tezov.tuucho.core.domain._system.find
import com.tezov.tuucho.core.domain.schema._element.label.LabelSchema
import com.tezov.tuucho.core.domain.schema.common.TypeSchema
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

class ContentLabelTextMatcher : Matcher {

    override fun accept(path: JsonElementPath, element: JsonElement): Boolean {
        if (!path.lastSegmentIs(LabelSchema.Name.Key)) return false
        val parent = element.find(path.parent()) as? JsonObject
        return parent.isSubsetOf(LabelSchema.Value.subset)
                && parent.isTypeOf(TypeSchema.Value.Type.content)
    }

}