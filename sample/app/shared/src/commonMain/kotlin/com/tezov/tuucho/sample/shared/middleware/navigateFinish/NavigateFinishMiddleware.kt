package com.tezov.tuucho.sample.shared.middleware.navigateFinish

import com.tezov.tuucho.core.domain.business.middleware.NavigationMiddleware
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.flow.FlowCollector

class NavigateFinishMiddleware(
    private val navigationFinishPublisher: NavigationFinishPublisher
) : NavigationMiddleware.Finish {

    override suspend fun process(
        context: Unit,
        next: MiddlewareProtocol.Next<Unit>?,
    ) {
        next?.invoke(Unit)
        navigationFinishPublisher.finish()
    }
}
