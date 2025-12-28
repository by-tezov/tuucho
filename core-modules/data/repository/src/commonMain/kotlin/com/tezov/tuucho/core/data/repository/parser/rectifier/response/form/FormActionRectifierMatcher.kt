package com.tezov.tuucho.core.data.repository.parser.rectifier.response.form

import com.tezov.tuucho.core.data.repository.parser._system.isSubsetOf
import com.tezov.tuucho.core.data.repository.parser._system.lastSegmentIs
import com.tezov.tuucho.core.data.repository.parser.rectifier.material._system.RectifierMatcherProtocol
import com.tezov.tuucho.core.domain.business.jsonSchema.response.FormSendSchema
import com.tezov.tuucho.core.domain.tool.annotation.TuuchoExperimentalAPI
import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import com.tezov.tuucho.core.domain.tool.json.find
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

@OptIn(TuuchoExperimentalAPI::class)
class FormActionRectifierMatcher : RectifierMatcherProtocol {
    override fun accept(
        path: JsonElementPath,
        element: JsonElement
    ): Boolean {
        if (!path.lastSegmentIs(FormSendSchema.Key.action)) return false
        val parent = element.find(path) as? JsonObject
        return parent.isSubsetOf(FormSendSchema.Value.subset)
    }
}
