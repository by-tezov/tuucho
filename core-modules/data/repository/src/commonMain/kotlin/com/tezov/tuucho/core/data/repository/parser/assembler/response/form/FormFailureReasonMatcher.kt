package com.tezov.tuucho.core.data.repository.parser.assembler.response.form

import com.tezov.tuucho.core.data.repository.parser.assembler.material._system.MatcherAssemblerProtocol
import com.tezov.tuucho.core.domain.tool.annotation.TuuchoExperimentalAPI
import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import kotlinx.serialization.json.JsonElement
import com.tezov.tuucho.core.data.repository.parser.rectifier.response.form.FormFailureReasonMatcher as FormFailureReasonMatcherRectifier

@OptIn(TuuchoExperimentalAPI::class)
class FormFailureReasonTextMatcher : MatcherAssemblerProtocol {
    private val matcher = FormFailureReasonMatcherRectifier()

    override fun accept(
        path: JsonElementPath,
        element: JsonElement
    ) = matcher.accept(path, element)
}
