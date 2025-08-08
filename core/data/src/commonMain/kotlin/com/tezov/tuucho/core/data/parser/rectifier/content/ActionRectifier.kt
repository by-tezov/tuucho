package com.tezov.tuucho.core.data.parser.rectifier.content

import com.tezov.tuucho.core.data.di.MaterialRectifierModule
import com.tezov.tuucho.core.data.parser.rectifier.Rectifier
import com.tezov.tuucho.core.data.parser.rectifier._system.MatcherRectifierProtocol
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.content.action.ActionSchema
import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import com.tezov.tuucho.core.domain.tool.json.find
import com.tezov.tuucho.core.domain.tool.json.string
import kotlinx.serialization.json.JsonElement
import org.koin.core.component.inject

class ActionRectifier : Rectifier() {

    override val matchers: List<MatcherRectifierProtocol> by inject(
        MaterialRectifierModule.Name.Matcher.ACTION
    )

    override fun beforeAlterPrimitive(
        path: JsonElementPath,
        element: JsonElement,
    ) = element.find(path).withScope(ActionSchema::Scope).apply {
        value = this.element.string
    }.collect()

}