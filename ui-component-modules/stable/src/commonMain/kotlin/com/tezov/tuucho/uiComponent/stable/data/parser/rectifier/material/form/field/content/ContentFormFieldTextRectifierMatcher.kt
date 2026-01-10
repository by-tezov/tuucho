package com.tezov.tuucho.uiComponent.stable.data.parser.rectifier.material.form.field.content

import com.tezov.tuucho.core.data.repository.parser._system.isSubsetOf
import com.tezov.tuucho.core.data.repository.parser._system.isTypeOf
import com.tezov.tuucho.core.data.repository.parser._system.lastSegmentIsAny
import com.tezov.tuucho.core.data.repository.parser.rectifier.material._system.RectifierMatcherProtocol
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import com.tezov.tuucho.core.domain.tool.json.find
import com.tezov.tuucho.uiComponent.stable.domain.jsonSchema.material.form.FormFieldSchema
import kotlinx.serialization.json.JsonElement

class ContentFormFieldTextRectifierMatcher : RectifierMatcherProtocol {
    private val segments = listOf(
        FormFieldSchema.Content.Key.title,
        FormFieldSchema.Content.Key.placeholder,
    )

    override fun accept(
        path: JsonElementPath,
        element: JsonElement
    ): Boolean {
        if (!path.lastSegmentIsAny(segments)) return false
        val parent = element.find(path.parent())
        return parent.isSubsetOf(FormFieldSchema.Component.Value.subset) &&
            parent.isTypeOf(TypeSchema.Value.content)
    }
}
