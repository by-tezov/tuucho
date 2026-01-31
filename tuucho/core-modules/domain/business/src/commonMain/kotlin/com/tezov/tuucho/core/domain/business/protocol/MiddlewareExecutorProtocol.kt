package com.tezov.tuucho.core.domain.business.protocol

import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow

interface MiddlewareExecutorProtocol {
    suspend fun <C, R : Any> ProducerScope<R>.process(
        middlewares: List<MiddlewareProtocol<C, R>>,
        context: C
    )

    companion object {
        fun <C, R : Any> MiddlewareExecutorProtocol.process(
            middlewares: List<MiddlewareProtocol<C, R>>,
            context: C
        ): Flow<R> = channelFlow {
            process(middlewares, context)
        }
    }
}
