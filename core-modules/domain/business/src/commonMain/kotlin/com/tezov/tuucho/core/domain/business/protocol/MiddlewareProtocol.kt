package com.tezov.tuucho.core.domain.business.protocol

import com.tezov.tuucho.core.domain.business.exception.DomainException

fun interface MiddlewareProtocol<C, R> {
    fun interface Next<C, R> {
        suspend fun invoke(
            context: C
        ): R
    }

    suspend fun process(
        context: C,
        next: Next<C, R>
    ): R

    companion object {
        suspend fun <C, R> List<MiddlewareProtocol<C, R>>.execute(
            context: C,
            terminal: MiddlewareProtocol<C, R>
        ): R {
            val initial: Next<C, R> = Next { context ->
                terminal.process(context, next = { throw DomainException.Default("Should never be called") })
            }
            val composed = foldRight(initial) { middleware, next ->
                Next { context -> middleware.process(context, next) }
            }
            return composed.invoke(context)
        }
    }
}
