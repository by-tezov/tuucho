package com.tezov.tuucho.core.data.repository.parser.breaker

import com.tezov.tuucho.core.data.repository.di.MaterialBreakerModule.Name
import com.tezov.tuucho.core.data.repository.parser.breaker._system.MatcherBreakerProtocol

import org.koin.core.component.inject

class TextBreaker : AbstractBreaker() {

    override val matchers: List<MatcherBreakerProtocol> by inject(
        Name.Matcher.TEXT
    )

    override val childProcessors: List<AbstractBreaker> by inject(
        Name.Processor.TEXT
    )

}