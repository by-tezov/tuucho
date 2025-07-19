package com.tezov.tuucho.core.data.parser.rectifier

import com.tezov.tuucho.core.data.di.MaterialRectifierModule.Name
import com.tezov.tuucho.core.data.parser._system.MatcherProtocol
import com.tezov.tuucho.core.domain._system.JsonElementPath
import com.tezov.tuucho.core.domain._system.find
import com.tezov.tuucho.core.domain._system.string
import com.tezov.tuucho.core.domain.model.schema._system.Schema.Companion.schema
import com.tezov.tuucho.core.domain.model.schema.material.ActionSchema
import kotlinx.serialization.json.JsonElement
import org.koin.core.component.inject

class ActionRectifier : Rectifier() {

    override val matchers: List<MatcherProtocol> by inject(
        Name.Matcher.ACTION
    )

    override fun beforeAlterPrimitive(
        path: JsonElementPath,
        element: JsonElement,
    ) = element.find(path).schema().withScope(ActionSchema::Scope).apply {
        value = this.element.string
    }.collect()

}