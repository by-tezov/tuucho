package com.tezov.tuucho.core.data.parser.breaker

import com.tezov.tuucho.core.data.di.MaterialBreakerModule.Name
import com.tezov.tuucho.core.data.parser.breaker._system.MatcherBreakerProtocol
import org.koin.core.component.inject

class OptionBreaker : Breaker() {

    override val matchers: List<MatcherBreakerProtocol> by inject(
        Name.Matcher.OPTION
    )

    override val childProcessors: List<Breaker> by inject(
        Name.Processor.OPTION
    )

}