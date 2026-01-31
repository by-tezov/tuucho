package com.tezov.tuucho.core.domain.business.protocol

import kotlinx.coroutines.flow.Flow

interface MiddlewareExecutorProtocol {
    suspend fun <C, R : Any> process(
        coroutineContext: CoroutineContextProtocol,
        middlewares: List<MiddlewareProtocol<C, R>>,
        context: C
    ): Flow<R>
}
