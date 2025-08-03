package com.tezov.tuucho.core.data.parser.rectifier._element.form.field.option


import com.tezov.tuucho.core.data.parser._system.isSubsetOf
import com.tezov.tuucho.core.data.parser._system.isTypeOf
import com.tezov.tuucho.core.data.parser._system.lastSegmentIs
import com.tezov.tuucho.core.data.parser.rectifier._system.MatcherRectifierProtocol
import com.tezov.tuucho.core.domain.business._system.JsonElementPath
import com.tezov.tuucho.core.domain.business._system.find
import com.tezov.tuucho.core.domain.business.model.schema.material.TypeSchema
import com.tezov.tuucho.core.domain.business.model.schema.material._element.form.FormFieldSchema
import com.tezov.tuucho.core.domain.business.model.schema.material._element.form.FormSchema
import kotlinx.serialization.json.JsonElement

class OptionFormFieldValidatorMatcher : MatcherRectifierProtocol {

    override fun accept(path: JsonElementPath, element: JsonElement): Boolean {
        if (!path.lastSegmentIs(FormSchema.Option.Key.validator)) return false
        val parent = element.find(path.parent())
        return parent.isTypeOf(TypeSchema.Value.option)
                && parent.isSubsetOf(FormFieldSchema.Component.Value.subset)
    }

}