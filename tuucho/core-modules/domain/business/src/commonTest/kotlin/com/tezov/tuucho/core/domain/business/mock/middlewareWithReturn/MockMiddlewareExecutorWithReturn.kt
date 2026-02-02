package com.tezov.tuucho.core.domain.business.mock.middlewareWithReturn

import com.tezov.tuucho.core.domain.business.protocol.MiddlewareExecutorProtocolWithReturn
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocolWithReturn
import kotlinx.coroutines.flow.channelFlow

class MockMiddlewareExecutorWithReturn : MiddlewareExecutorProtocolWithReturn {
    var process: MiddlewareExecutorProtocolWithReturn = object :
        MiddlewareExecutorProtocolWithReturn {
        override suspend fun <C, R : Any> process(
            middlewares: List<MiddlewareProtocolWithReturn<C, R>>,
            context: C
        ) = channelFlow {
            middlewares.forEach { it.run { process(context, null) } }
        }
    }


    override suspend fun <C, R : Any> process(
        middlewares: List<MiddlewareProtocolWithReturn<C, R>>,
        context: C
    ) = process.process(middlewares, context)
}
