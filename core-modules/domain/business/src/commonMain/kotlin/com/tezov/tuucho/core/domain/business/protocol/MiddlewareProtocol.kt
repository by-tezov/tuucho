package com.tezov.tuucho.core.domain.business.protocol

import com.tezov.tuucho.core.domain.business.exception.DomainException

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

    companion object {
        suspend fun <C, R> List<MiddlewareProtocol<C, R>>.execute(
            context: C
        ): R? {
            var next: Next<C, R>? = null
            for (middleware in asReversed()) {
                val prev = next
                var invoked = false
                next = Next { context ->
                    if (invoked) throw DomainException.Default("next invoked multiple times")
                    invoked = true
                    middleware.process(context, prev)
                }
            }
            return next?.invoke(context)
        }
    }
}
