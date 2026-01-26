package com.tezov.tuucho.core.domain.business.mock

import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol.Next.Companion.invoke
import kotlinx.coroutines.flow.FlowCollector

typealias ProcessStringMiddlewareTypeAlias = suspend FlowCollector<String>.(
    context: String,
    next: MiddlewareProtocol.Next<String, String>?
) -> Unit

class MockStringMiddleware(
    val command: String,
    val emitBefore: Boolean = true,
    val callNext: Boolean = true
) : MiddlewareProtocol<String, String> {
    var process: ProcessStringMiddlewareTypeAlias = { context, next ->
        if (emitBefore) {
            emit(command)
        }
        if (callNext) {
            next?.invoke(context)
        }
        if (!emitBefore) {
            emit(command)
        }
    }

    override suspend fun FlowCollector<String>.process(
        context: String,
        next: MiddlewareProtocol.Next<String, String>?
    ) {
        process.invoke(this@process, context, next)
    }
}
