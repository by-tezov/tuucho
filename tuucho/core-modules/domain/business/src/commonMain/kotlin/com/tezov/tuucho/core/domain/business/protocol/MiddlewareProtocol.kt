package com.tezov.tuucho.core.domain.business.protocol

fun interface MiddlewareProtocol<C, R> {
    fun interface Next<C, R> {
        suspend fun invoke(
            context: C
        ): R?
    }

    suspend fun process(
        context: C,
        next: Next<C, R>?
    ): R?
}
