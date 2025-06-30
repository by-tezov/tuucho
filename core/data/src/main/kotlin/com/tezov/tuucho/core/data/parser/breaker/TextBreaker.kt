package com.tezov.tuucho.core.data.parser.breaker

import com.tezov.tuucho.core.data.di.MaterialBreakerModule.Name
import com.tezov.tuucho.core.data.parser._system.MatcherProtocol
import com.tezov.tuucho.core.data.parser._system.isTypeOf
import com.tezov.tuucho.core.domain._system.JsonElementPath
import com.tezov.tuucho.core.domain.schema.TypeSchema
import kotlinx.serialization.json.JsonElement
import org.koin.core.component.inject

class TextBreaker : Breaker() {

    override val matchers: List<MatcherProtocol> by inject(
        Name.Matcher.TEXT
    )

    override val childProcessors: List<Breaker> by inject(
        Name.Processor.TEXT
    )

    override fun accept(
        path: JsonElementPath, element: JsonElement
    ) = path.isTypeOf(element, TypeSchema.Value.Type.text) || super.accept(path, element)

}