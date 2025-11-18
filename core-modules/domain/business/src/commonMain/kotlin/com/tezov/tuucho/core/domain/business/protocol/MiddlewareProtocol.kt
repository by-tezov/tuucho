package com.tezov.tuucho.core.domain.business.protocol

fun interface MiddlewareProtocol<C, R> {
    fun interface Next<C, R> {
        suspend fun invoke(
            context: C
        ): R?
    }

    suspend fun process(
        context: C,
        next: Next<C, R>
    ): R?

    companion object {
        suspend fun <C, R> List<MiddlewareProtocol<C, R>>.execute(
            context: C
        ): R? {
            var result: R? = null
            var next = Next<C, R> { result } // endpoint, loopback
            for (middleware in asReversed()) {
                val prev = next
                next = Next { context ->
                    middleware.process(context, prev).also { result = it }
                }
            }
            return next.invoke(context)
        }
    }
}
