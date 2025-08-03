package com.tezov.tuucho.core.data.parser.rectifier._element.form


import com.tezov.tuucho.core.data.parser._system.isSubsetStartWith
import com.tezov.tuucho.core.data.parser._system.isTypeOf
import com.tezov.tuucho.core.data.parser._system.lastSegmentIs
import com.tezov.tuucho.core.data.parser.rectifier._system.MatcherRectifierProtocol
import com.tezov.tuucho.core.domain.business._system.JsonElementPath
import com.tezov.tuucho.core.domain.business._system.find
import com.tezov.tuucho.core.domain.business.model.schema.material.TypeSchema
import com.tezov.tuucho.core.domain.business.model.schema.material._element.form.FormSchema
import kotlinx.serialization.json.JsonElement

class StateFormTextMatcher : MatcherRectifierProtocol {

    override fun accept(path: JsonElementPath, element: JsonElement): Boolean {
        if (!path.lastSegmentIs(FormSchema.State.Key.initialValue)) return false
        val parent = element.find(path.parent())
        return parent.isSubsetStartWith(FormSchema.Component.Value.subset)
                && parent.isTypeOf(TypeSchema.Value.content)
    }

}