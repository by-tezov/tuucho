package com.tezov.tuucho.core.data.parser.rectifier._element.field.option

import com.tezov.tuucho.core.data.parser._system.MatcherProtocol
import com.tezov.tuucho.core.data.parser._system.isSubsetOf
import com.tezov.tuucho.core.data.parser._system.isTypeOf
import com.tezov.tuucho.core.data.parser._system.lastSegmentIs
import com.tezov.tuucho.core.domain._system.JsonElementPath
import com.tezov.tuucho.core.domain._system.find
import com.tezov.tuucho.core.domain.model.schema.material.TypeSchema
import com.tezov.tuucho.core.domain.model.schema.material._element.FieldSchema
import kotlinx.serialization.json.JsonElement

class OptionFieldValidatorMatcher : MatcherProtocol {

    override fun accept(path: JsonElementPath, element: JsonElement): Boolean {
        if (!path.lastSegmentIs(FieldSchema.Option.Key.validator)) return false
        val parent = element.find(path.parent())
        return parent.isTypeOf(TypeSchema.Value.option)
                && parent.isSubsetOf(FieldSchema.Component.Value.subset)
    }

}