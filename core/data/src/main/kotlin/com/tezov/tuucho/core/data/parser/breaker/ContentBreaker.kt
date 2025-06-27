package com.tezov.tuucho.core.data.parser.breaker

import com.tezov.tuucho.core.data.di.MaterialBreakerModule.Name
import com.tezov.tuucho.core.data.parser._system.MatcherProtocol
import com.tezov.tuucho.core.data.parser._system.isTypeOf
import com.tezov.tuucho.core.domain._system.JsonElementPath
import com.tezov.tuucho.core.domain.schema.common.TypeSchema
import kotlinx.serialization.json.JsonElement
import org.koin.core.component.inject

class ContentBreaker : Breaker() {

    override val matchers: List<MatcherProtocol> by inject(
        Name.Matcher.CONTENT
    )

    override val childProcessors: List<Breaker> by inject(
        Name.Processor.CONTENT
    )

    override fun accept(
        path: JsonElementPath, element: JsonElement
    ) = path.isTypeOf(element, TypeSchema.Value.Type.content) || super.accept(path, element)

}