package com.tezov.tuucho.core.domain.business.protocol

import kotlinx.coroutines.flow.Flow

interface MiddlewareExecutorProtocol {
    suspend fun <C> process(
        middlewares: List<MiddlewareProtocol<C>>,
        context: C
    )
}

interface MiddlewareExecutorProtocolWithReturn {
    suspend fun <C, R : Any> process(
        middlewares: List<MiddlewareProtocolWithReturn<C, R>>,
        context: C
    ): Flow<R>
}
