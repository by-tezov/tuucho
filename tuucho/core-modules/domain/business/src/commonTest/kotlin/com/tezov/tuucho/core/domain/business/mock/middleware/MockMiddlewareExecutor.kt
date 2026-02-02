package com.tezov.tuucho.core.domain.business.mock.middleware

import com.tezov.tuucho.core.domain.business.protocol.MiddlewareExecutorProtocol
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol

class MockMiddlewareExecutor : MiddlewareExecutorProtocol {
    var process: MiddlewareExecutorProtocol = object : MiddlewareExecutorProtocol {
        override suspend fun <C> process(
            middlewares: List<MiddlewareProtocol<C>>,
            context: C
        ) {
            middlewares.forEach { it.run { process(context, null) } }
        }
    }

    override suspend fun <C> process(
        middlewares: List<MiddlewareProtocol<C>>,
        context: C
    ) {
        process.run { process(middlewares, context) }
    }
}
