package com.tezov.tuucho.uiComponent.stable.data.parser.assembler.material.form

import com.tezov.tuucho.core.data.repository.parser.assembler.material._system.AssemblerMatcherProtocol
import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import com.tezov.tuucho.uiComponent.stable.data.parser.rectifier.material.form.field.content.ContentFormFieldTextErrorRectifierMatcher
import kotlinx.serialization.json.JsonElement

class ContentFormFieldTextErrorAssemblerMatcher : AssemblerMatcherProtocol {
    private val matcher = ContentFormFieldTextErrorRectifierMatcher()

    override fun accept(
        path: JsonElementPath,
        element: JsonElement
    ) = matcher.accept(path, element)
}


