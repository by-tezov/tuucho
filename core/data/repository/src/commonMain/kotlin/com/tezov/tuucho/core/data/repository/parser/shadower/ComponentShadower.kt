package com.tezov.tuucho.core.data.repository.parser.shadower

import com.tezov.tuucho.core.data.repository.di.MaterialShadowerModule.Name
import com.tezov.tuucho.core.data.repository.parser._system.isTypeOf
import com.tezov.tuucho.core.data.repository.parser.shadower._system.MatcherShadowerProtocol
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import kotlinx.serialization.json.JsonElement
import org.koin.core.component.inject

class ComponentShadower: AbstractShadower() {

    override val matchers: List<MatcherShadowerProtocol> by inject(
        Name.Matcher.COMPONENT
    )

    override val childProcessors: List<AbstractShadower> by inject(
        Name.Processor.COMPONENT
    )

    override fun accept(
        path: JsonElementPath, element: JsonElement
    ) = path.isTypeOf(element, TypeSchema.Value.component) || super.accept(path, element)

}