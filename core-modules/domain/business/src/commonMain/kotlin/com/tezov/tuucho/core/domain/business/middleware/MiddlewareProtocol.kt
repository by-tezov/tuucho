package com.tezov.tuucho.core.domain.business.middleware

fun interface NextMiddleware<C> {
    suspend operator fun invoke(
        context: C
    )
}

fun interface MiddlewareProtocol<C : Any> {
    companion object {
        suspend fun <C : Any> List<MiddlewareProtocol<C>>.execute(
            context: C,
        ) {
            val terminal: suspend (C) -> Unit = {}
            val composed = foldRight(terminal) { middleware, next ->
                { context -> middleware.process(context, NextMiddleware(next)) }
            }
            composed(context)
        }
    }

    suspend fun process(
        context: C,
        next: NextMiddleware<C>
    )
}
