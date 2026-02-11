package com.tezov.tuucho.core.domain.business.interaction.middleware

import com.tezov.tuucho.core.domain.business.protocol.MiddlewareExecutorProtocol
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareExecutorProtocolWithReturn
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocolWithReturn
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocolWithReturn.Next.Companion.invoke
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow

class MiddlewareExecutor : MiddlewareExecutorProtocol {
    override suspend fun <C> process(
        middlewares: List<MiddlewareProtocol<C>>,
        context: C
    ) {
        var next: MiddlewareProtocol.Next<C>? = null
        for (middleware in middlewares.asReversed()) {
            val prev = next
            next = MiddlewareProtocol.Next { context ->
                middleware.run { process(context, prev) }
            }
        }
        next?.invoke(context)
    }
}

class MiddlewareExecutorWithReturn : MiddlewareExecutorProtocolWithReturn {
    override suspend fun <C, R : Any> process(
        middlewares: List<MiddlewareProtocolWithReturn<C, R>>,
        context: C
    ): Flow<R> {
        var next: MiddlewareProtocolWithReturn.Next<C, R>? = null
        for (middleware in middlewares.asReversed()) {
            val prev = next
            next = MiddlewareProtocolWithReturn.Next { context ->
                middleware.run { process(context, prev) }
            }
        }
        return channelFlow {
            runCatching { next?.invoke(context) }
                .onFailure { close(it) }
                .onSuccess { close() }
        }
    }
}
