@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.data.repository.parser.rectifier.material._element.form.field.option

import com.tezov.tuucho.core.data.repository.parser._system.isSubsetOf
import com.tezov.tuucho.core.data.repository.parser._system.isTypeOf
import com.tezov.tuucho.core.data.repository.parser._system.lastSegmentIs
import com.tezov.tuucho.core.data.repository.parser.rectifier.material._system.MatcherRectifierProtocol
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material._element.form.FormFieldSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material._element.form.FormSchema
import com.tezov.tuucho.core.domain.tool.annotation.TuuchoExperimentalAPI
import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import com.tezov.tuucho.core.domain.tool.json.find
import kotlinx.serialization.json.JsonElement

@OptIn(TuuchoExperimentalAPI::class)
class OptionFormFieldValidatorMatcher : MatcherRectifierProtocol {
    override fun accept(
        path: JsonElementPath,
        element: JsonElement
    ): Boolean {
        if (!path.lastSegmentIs(FormSchema.Option.Key.validator)) return false
        val parent = element.find(path.parent())
        return parent.isTypeOf(TypeSchema.Value.option) &&
            parent.isSubsetOf(FormFieldSchema.Component.Value.subset)
    }
}
