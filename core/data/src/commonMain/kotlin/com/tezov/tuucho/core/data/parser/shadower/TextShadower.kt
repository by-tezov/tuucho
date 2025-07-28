package com.tezov.tuucho.core.data.parser.shadower

import com.tezov.tuucho.core.data.di.MaterialShadowerModule.Name
import com.tezov.tuucho.core.data.parser._system.isTypeOf
import com.tezov.tuucho.core.data.parser.shadower._system.MatcherShadowerProtocol
import com.tezov.tuucho.core.domain._system.JsonElementPath
import com.tezov.tuucho.core.domain.model.schema.material.TypeSchema
import kotlinx.serialization.json.JsonElement
import org.koin.core.component.inject

class TextShadower : Shadower() {

    override val matchers: List<MatcherShadowerProtocol> by inject(
        Name.Matcher.TEXT
    )

    override val childProcessors: List<Shadower> by inject(
        Name.Processor.TEXT
    )

    override fun accept(
        path: JsonElementPath, element: JsonElement
    ) = path.isTypeOf(element, TypeSchema.Value.text) || super.accept(path, element)

}