package com.tezov.tuucho.core.domain.business.mock.middlewareWithReturn

import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocolWithReturn
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocolWithReturn.Next.Companion.invoke
import kotlinx.coroutines.channels.ProducerScope

typealias ProcessStringMiddlewareWithReturnTypeAlias = suspend ProducerScope<String>.(
    context: String,
    next: MiddlewareProtocolWithReturn.Next<String, String>?
) -> Unit

class MockStringMiddlewareWithReturn(
    val command: String,
    val sendBefore: Boolean = true,
    val callNext: Boolean = true
) : MiddlewareProtocolWithReturn<String, String> {
    var process: ProcessStringMiddlewareWithReturnTypeAlias = { context, next ->
        if (sendBefore) {
            send(command)
        }
        if (callNext) {
            next?.invoke(context)
        }
        if (!sendBefore) {
            send(command)
        }
    }

    override suspend fun ProducerScope<String>.process(
        context: String,
        next: MiddlewareProtocolWithReturn.Next<String, String>?
    ) {
        process.invoke(this@process, context, next)
    }
}
