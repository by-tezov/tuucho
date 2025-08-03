package com.tezov.tuucho.core.data.parser.shadower

import com.tezov.tuucho.core.data.di.MaterialShadowerModule.Name
import com.tezov.tuucho.core.data.parser._system.isTypeOf
import com.tezov.tuucho.core.data.parser.shadower._system.MatcherShadowerProtocol
import com.tezov.tuucho.core.domain.business._system.JsonElementPath
import com.tezov.tuucho.core.domain.business.model.schema.material.TypeSchema
import kotlinx.serialization.json.JsonElement
import org.koin.core.component.inject

class ContentShadower : Shadower() {

    override val matchers: List<MatcherShadowerProtocol> by inject(
        Name.Matcher.CONTENT
    )

    override val childProcessors: List<Shadower> by inject(
        Name.Processor.CONTENT
    )

    override fun accept(
        path: JsonElementPath, element: JsonElement
    ) = path.isTypeOf(element, TypeSchema.Value.content) || super.accept(path, element)

}