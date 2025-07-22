package com.tezov.tuucho.core.data.parser.rectifier._element.field.content

import com.tezov.tuucho.core.data.parser._system.MatcherProtocol
import com.tezov.tuucho.core.data.parser._system.isSubsetOf
import com.tezov.tuucho.core.data.parser._system.isTypeOf
import com.tezov.tuucho.core.data.parser._system.lastSegmentIsAny
import com.tezov.tuucho.core.domain._system.JsonElementPath
import com.tezov.tuucho.core.domain._system.find
import com.tezov.tuucho.core.domain.model.schema.material.TypeSchema
import com.tezov.tuucho.core.domain.model.schema.material._element.FieldSchema
import kotlinx.serialization.json.JsonElement

class ContentFieldTextMatcher : MatcherProtocol {

    private val segments = listOf(
        FieldSchema.Content.Key.title,
        FieldSchema.Content.Key.placeholder,
    )

    override fun accept(path: JsonElementPath, element: JsonElement): Boolean {
        if (!path.lastSegmentIsAny(segments)) return false
        val parent = element.find(path.parent())
        return parent.isSubsetOf(FieldSchema.Component.Value.subset)
                && parent.isTypeOf(TypeSchema.Value.content)
    }

}