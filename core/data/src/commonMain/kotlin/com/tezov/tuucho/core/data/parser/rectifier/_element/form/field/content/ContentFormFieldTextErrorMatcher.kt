package com.tezov.tuucho.core.data.parser.rectifier._element.form.field.content


import com.tezov.tuucho.core.data.parser._system.isSubsetOf
import com.tezov.tuucho.core.data.parser._system.isTypeOf
import com.tezov.tuucho.core.data.parser._system.lastSegmentIs
import com.tezov.tuucho.core.data.parser.rectifier._system.MatcherRectifierProtocol
import com.tezov.tuucho.core.domain._system.JsonElementPath
import com.tezov.tuucho.core.domain._system.find
import com.tezov.tuucho.core.domain.model.schema.material.TypeSchema
import com.tezov.tuucho.core.domain.model.schema.material._element.form.FormFieldSchema
import kotlinx.serialization.json.JsonElement

class ContentFormFieldTextErrorMatcher : MatcherRectifierProtocol {

    override fun accept(path: JsonElementPath, element: JsonElement): Boolean {
        if (!path.lastSegmentIs(FormFieldSchema.Content.Key.messageError)) return false
        val parent = element.find(path.parent())
        return parent.isSubsetOf(FormFieldSchema.Component.Value.subset)
                && parent.isTypeOf(TypeSchema.Value.content)
    }

}