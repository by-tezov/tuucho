package com.tezov.tuucho.core.data.parser.rectifier

import com.tezov.tuucho.core.data.di.MaterialRectifierModule.Name
import com.tezov.tuucho.core.data.parser.rectifier._system.MatcherRectifierProtocol

import com.tezov.tuucho.core.domain.business._system.JsonElementPath
import com.tezov.tuucho.core.domain.business._system.find
import com.tezov.tuucho.core.domain.business._system.string
import com.tezov.tuucho.core.domain.business.model.schema._system.withScope

import com.tezov.tuucho.core.domain.business.model.schema.material.ActionSchema
import kotlinx.serialization.json.JsonElement
import org.koin.core.component.inject

class ActionRectifier : Rectifier() {

    override val matchers: List<MatcherRectifierProtocol> by inject(
        Name.Matcher.ACTION
    )

    override fun beforeAlterPrimitive(
        path: JsonElementPath,
        element: JsonElement,
    ) = element.find(path).withScope(ActionSchema::Scope).apply {
        value = this.element.string
    }.collect()

}