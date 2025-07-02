package com.tezov.tuucho.core.data.parser.breaker

import com.tezov.tuucho.core.data.di.MaterialBreakerModule.Name
import com.tezov.tuucho.core.data.parser._system.MatcherProtocol
import org.koin.core.component.inject

class ColorBreaker: Breaker() {

    override val matchers: List<MatcherProtocol> by inject(
        Name.Matcher.COLOR
    )

    override val childProcessors: List<Breaker> by inject(
        Name.Processor.COLOR
    )

}