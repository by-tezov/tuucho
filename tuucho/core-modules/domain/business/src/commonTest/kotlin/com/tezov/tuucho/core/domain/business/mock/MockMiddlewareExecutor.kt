package com.tezov.tuucho.core.domain.business.mock

import com.tezov.tuucho.core.domain.business.protocol.MiddlewareExecutorProtocol
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol
import kotlinx.coroutines.flow.FlowCollector

class MockMiddlewareExecutor : MiddlewareExecutorProtocol {
    var process: MiddlewareExecutorProtocol = object : MiddlewareExecutorProtocol {
        override suspend fun <C, R : Any> FlowCollector<R>.process(
            middlewares: List<MiddlewareProtocol<C, R>>,
            context: C
        ) {
            middlewares.forEach { it.run { process(context, null) } }
        }
    }

    override suspend fun <C, R : Any> FlowCollector<R>.process(
        middlewares: List<MiddlewareProtocol<C, R>>,
        context: C
    ) {
        process.run { process(middlewares, context) }
    }
}
