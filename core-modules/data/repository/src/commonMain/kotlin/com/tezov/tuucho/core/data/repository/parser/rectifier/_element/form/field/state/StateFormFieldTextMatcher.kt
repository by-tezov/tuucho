package com.tezov.tuucho.core.data.repository.parser.rectifier._element.form.field.state

import com.tezov.tuucho.core.data.repository.parser._system.isSubsetStartWith
import com.tezov.tuucho.core.data.repository.parser._system.isTypeOf
import com.tezov.tuucho.core.data.repository.parser._system.lastSegmentIs
import com.tezov.tuucho.core.data.repository.parser.rectifier._system.MatcherRectifierProtocol
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material._element.form.FormFieldSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material._element.form.FormSchema
import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import com.tezov.tuucho.core.domain.tool.json.find
import kotlinx.serialization.json.JsonElement

class StateFormFieldTextMatcher : MatcherRectifierProtocol {

    override fun accept(path: JsonElementPath, element: JsonElement): Boolean {
        if (!path.lastSegmentIs(FormFieldSchema.State.Key.initialValue)) return false
        val parent = element.find(path.parent())
        return parent.isSubsetStartWith(FormSchema.Component.Value.subset)
                && parent.isTypeOf(TypeSchema.Value.content)
    }

}