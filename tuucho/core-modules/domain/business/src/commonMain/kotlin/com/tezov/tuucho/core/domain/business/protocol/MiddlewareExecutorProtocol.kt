package com.tezov.tuucho.core.domain.business.protocol

interface MiddlewareExecutorProtocol {
    suspend fun <C, R> process(
        middlewares: List<MiddlewareProtocol<C, R>>,
        context: C
    ): R?
}
