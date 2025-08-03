package com.tezov.tuucho.core.data.parser.rectifier._element.form.field.content


import com.tezov.tuucho.core.data.parser._system.isSubsetOf
import com.tezov.tuucho.core.data.parser._system.isTypeOf
import com.tezov.tuucho.core.data.parser._system.lastSegmentIsAny
import com.tezov.tuucho.core.data.parser.rectifier._system.MatcherRectifierProtocol
import com.tezov.tuucho.core.domain.business._system.JsonElementPath
import com.tezov.tuucho.core.domain.business._system.find
import com.tezov.tuucho.core.domain.business.model.schema.material.TypeSchema
import com.tezov.tuucho.core.domain.business.model.schema.material._element.form.FormFieldSchema
import kotlinx.serialization.json.JsonElement

class ContentFormFieldTextMatcher : MatcherRectifierProtocol {

    private val segments = listOf(
        FormFieldSchema.Content.Key.title,
        FormFieldSchema.Content.Key.placeholder,
    )

    override fun accept(path: JsonElementPath, element: JsonElement): Boolean {
        if (!path.lastSegmentIsAny(segments)) return false
        val parent = element.find(path.parent())
        return parent.isSubsetOf(FormFieldSchema.Component.Value.subset)
                && parent.isTypeOf(TypeSchema.Value.content)
    }

}