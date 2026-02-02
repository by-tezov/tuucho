package com.tezov.tuucho.core.domain.business.mock.middleware

import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol

typealias ProcessStringMiddlewareTypeAlias = suspend (
    context: Int,
    next: MiddlewareProtocol.Next<Int>?
) -> Unit

class MockStringMiddleware(
    val command: String,
    val callNext: Boolean = true
) : MiddlewareProtocol<Int> {
    var commendEcho: String? = null
    var contextEcho: Int? = null

    var process: ProcessStringMiddlewareTypeAlias = { context, next ->
        contextEcho = context
        commendEcho = command
        if (callNext) {
            next?.invoke(context + 1)
        }
    }

    override suspend fun process(
        context: Int,
        next: MiddlewareProtocol.Next<Int>?
    ) {
        process.invoke(context, next)
    }
}
