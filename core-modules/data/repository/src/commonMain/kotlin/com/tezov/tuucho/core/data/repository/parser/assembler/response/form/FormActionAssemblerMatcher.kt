package com.tezov.tuucho.core.data.repository.parser.assembler.response.form

import com.tezov.tuucho.core.data.repository.parser.assembler.material._system.AssemblerMatcherProtocol
import com.tezov.tuucho.core.domain.tool.annotation.TuuchoExperimentalAPI
import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import kotlinx.serialization.json.JsonElement
import com.tezov.tuucho.core.data.repository.parser.rectifier.response.form.FormActionRectifierMatcher as FormActionMatcherRectifier

@OptIn(TuuchoExperimentalAPI::class)
class FormActionAssemblerMatcher : AssemblerMatcherProtocol {
    private val matcher = FormActionMatcherRectifier()

    override fun accept(
        path: JsonElementPath,
        element: JsonElement
    ) = matcher.accept(path, element)
}
