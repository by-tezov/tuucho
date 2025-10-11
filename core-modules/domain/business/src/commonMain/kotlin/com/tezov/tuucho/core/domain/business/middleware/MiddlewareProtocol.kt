package com.tezov.tuucho.core.domain.business.middleware

fun interface NextMiddleware<C> {
    suspend operator fun invoke(context: C)
}

fun interface MiddlewareProtocol<C : Any> {

    companion object {
        suspend fun <C : Any> List<MiddlewareProtocol<C>>.execute(
            context: C,
        ) {
            var next: NextMiddleware<C>? = null
            for (middleware in asReversed()) {
                val nextCaptured = next
                next = NextMiddleware {
                    context -> middleware.process(context, nextCaptured)
                }
            }
            next?.invoke(context)
        }
    }

    suspend fun process(context: C, next: NextMiddleware<C>?)
}