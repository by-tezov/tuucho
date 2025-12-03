package com.tezov.tuucho.core.data.repository.parser.rectifier.response.form

import com.tezov.tuucho.core.data.repository.parser._system.isSubsetOf
import com.tezov.tuucho.core.data.repository.parser._system.lastSegmentIs
import com.tezov.tuucho.core.data.repository.parser.rectifier.material._system.MatcherRectifierProtocol
import com.tezov.tuucho.core.domain.business.jsonSchema.response.FormSendSchema
import com.tezov.tuucho.core.domain.tool.annotation.TuuchoExperimentalAPI
import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import com.tezov.tuucho.core.domain.tool.json.find
import kotlinx.serialization.json.JsonElement

@OptIn(TuuchoExperimentalAPI::class)
class FormFailureReasonMatcher : MatcherRectifierProtocol {
    override fun accept(
        path: JsonElementPath,
        element: JsonElement
    ): Boolean {
        if (!path.lastSegmentIs(FormSendSchema.Key.failureResult)) return false
        val parent = element.find(path.parent())
        return parent.isSubsetOf(FormSendSchema.Value.subset)
    }
}
