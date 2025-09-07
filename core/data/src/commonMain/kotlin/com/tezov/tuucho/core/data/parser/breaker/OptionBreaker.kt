package com.tezov.tuucho.core.data.parser.breaker

import com.tezov.tuucho.core.data.di.MaterialBreakerModule.Name
import com.tezov.tuucho.core.data.parser.breaker._system.MatcherBreakerProtocol
import org.koin.core.component.inject

class OptionBreaker : AbstractBreaker() {

    override val matchers: List<MatcherBreakerProtocol> by inject(
        Name.Matcher.OPTION
    )

    override val childProcessors: List<AbstractBreaker> by inject(
        Name.Processor.OPTION
    )

}