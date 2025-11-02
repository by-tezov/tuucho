@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.data.repository.parser.rectifier._element.button.content.action

import com.tezov.tuucho.core.data.repository.parser._system.isSubsetOf
import com.tezov.tuucho.core.data.repository.parser._system.isTypeOf
import com.tezov.tuucho.core.data.repository.parser._system.lastSegmentIs
import com.tezov.tuucho.core.data.repository.parser.rectifier._system.MatcherRectifierProtocol
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material._element.ButtonSchema
import com.tezov.tuucho.core.domain.tool.annotation.TuuchoExperimentalAPI
import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import com.tezov.tuucho.core.domain.tool.json.find
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

@OptIn(TuuchoExperimentalAPI::class)
class ActionButtonMatcher : MatcherRectifierProtocol {
    override fun accept(
        path: JsonElementPath,
        element: JsonElement
    ): Boolean {
        if (!path.lastSegmentIs(ButtonSchema.Content.Key.action)) return false
        val parent = element.find(path.parent()) as? JsonObject
        return parent.isSubsetOf(ButtonSchema.Component.Value.subset) &&
            parent.isTypeOf(TypeSchema.Value.content)
    }
}
