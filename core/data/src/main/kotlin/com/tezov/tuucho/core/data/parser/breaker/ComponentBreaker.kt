package com.tezov.tuucho.core.data.parser.breaker

import com.tezov.tuucho.core.data.di.MaterialBreakerModule.Name
import com.tezov.tuucho.core.data.parser._system.Matcher
import org.koin.core.component.inject

class ComponentBreaker: Breaker() {

    override val matchers: List<Matcher> by inject(
        Name.Matcher.COMPONENT
    )

    override val childProcessors: List<Breaker> by inject(
        Name.Processor.COMPONENT
    )

}