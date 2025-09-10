package com.tezov.tuucho.core.data.parser.shadower

import com.tezov.tuucho.core.data.di.MaterialShadowerModule.Name
import com.tezov.tuucho.core.data.parser._system.isTypeOf
import com.tezov.tuucho.core.data.parser.shadower._system.MatcherShadowerProtocol
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import kotlinx.serialization.json.JsonElement
import org.koin.core.component.inject

class TextShadower : AbstractShadower() {

    override val matchers: List<MatcherShadowerProtocol> by inject(
        Name.Matcher.TEXT
    )

    override val childProcessors: List<AbstractShadower> by inject(
        Name.Processor.TEXT
    )

    override fun accept(
        path: JsonElementPath, element: JsonElement
    ) = path.isTypeOf(element, TypeSchema.Value.text) || super.accept(path, element)

}