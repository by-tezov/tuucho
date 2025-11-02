package com.tezov.tuucho.core.data.repository.parser.breaker

import com.tezov.tuucho.core.data.repository.di.MaterialBreakerModule.Name
import com.tezov.tuucho.core.data.repository.parser.breaker._system.AbstractBreaker
import com.tezov.tuucho.core.data.repository.parser.breaker._system.MatcherBreakerProtocol
import com.tezov.tuucho.core.domain.tool.annotation.TuuchoExperimentalAPI
import org.koin.core.component.inject

@OptIn(TuuchoExperimentalAPI::class)
class ContentBreaker : AbstractBreaker() {
    override val matchers: List<MatcherBreakerProtocol> by inject(
        Name.Matcher.CONTENT
    )

    override val childProcessors: List<AbstractBreaker> by inject(
        Name.Processor.CONTENT
    )
}
