package com.tezov.tuucho.core.data.parser.rectifier._element.field

import com.tezov.tuucho.core.data.parser._system.MatcherProtocol
import com.tezov.tuucho.core.data.parser._system.isSubsetOf
import com.tezov.tuucho.core.data.parser._system.isTypeOf
import com.tezov.tuucho.core.data.parser._system.lastSegmentIs
import com.tezov.tuucho.core.domain._system.JsonElementPath
import com.tezov.tuucho.core.domain._system.find
import com.tezov.tuucho.core.domain.schema.TypeSchema
import com.tezov.tuucho.core.domain.schema._element.FieldSchema
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

class ContentFieldTextErrorMatcher : MatcherProtocol {

    override fun accept(path: JsonElementPath, element: JsonElement): Boolean {
        if (!path.lastSegmentIs(FieldSchema.Content.Key.messageError)) return false
        val parent = element.find(path.parent()) as? JsonObject
        return parent.isSubsetOf(FieldSchema.Component.Value.subset)
                && parent.isTypeOf(TypeSchema.Value.Type.content)
    }

}