package com.tezov.tuucho.core.domain.business.protocol

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow

interface MiddlewareExecutorProtocol {
    suspend fun <C, R : Any> FlowCollector<R>.process(
        middlewares: List<MiddlewareProtocol<C, R>>,
        context: C
    )

    companion object {
        fun <C, R : Any> MiddlewareExecutorProtocol.process(
            middlewares: List<MiddlewareProtocol<C, R>>,
            context: C
        ): Flow<R> = flow { process(middlewares, context) }
    }
}
