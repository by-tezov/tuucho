package com.tezov.tuucho.core.domain.business.middleware

import com.tezov.tuucho.core.domain.business.protocol.MiddlewareExecutorProtocol
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol
import kotlinx.coroutines.flow.FlowCollector

class MiddlewareExecutor : MiddlewareExecutorProtocol {
    override suspend fun <C, R> FlowCollector<R>.process(
        middlewares: List<MiddlewareProtocol<C, R>>,
        context: C
    ) {
        var next: MiddlewareProtocol.Next<C, R>? = null
        for (middleware in middlewares.asReversed()) {
            val prev = next
            next = MiddlewareProtocol.Next { context ->
                middleware.run { process(context, prev) }
            }
        }
        next?.invoke(context)
    }
}
