package com.tezov.tuucho.core.domain.business.middleware

import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareExecutorProtocol
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol

class MiddlewareExecutor : MiddlewareExecutorProtocol {
    override suspend fun <C, R> process(
        middlewares: List<MiddlewareProtocol<C, R>>,
        context: C
    ): R? {
        var next: MiddlewareProtocol.Next<C, R>? = null
        for (middleware in middlewares.asReversed()) {
            val prev = next
            var invoked = false
            next = MiddlewareProtocol.Next { context ->
                if (invoked) throw DomainException.Default("next invoked multiple times")
                invoked = true
                middleware.process(context, prev)
            }
        }
        return next?.invoke(context)
    }
}
