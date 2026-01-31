package com.tezov.tuucho.core.domain.business.middleware

import com.tezov.tuucho.core.domain.business.protocol.CoroutineContextProtocol
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareExecutorProtocol
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol.Next.Companion.invoke
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow

class MiddlewareExecutor : MiddlewareExecutorProtocol {
    override suspend fun <C, R : Any> process(
        coroutineContext: CoroutineContextProtocol,
        middlewares: List<MiddlewareProtocol<C, R>>,
        context: C
    ): Flow<R> {
        var next: MiddlewareProtocol.Next<C, R>? = null
        for (middleware in middlewares.asReversed()) {
            val prev = next
            next = MiddlewareProtocol.Next { context ->
                middleware.run { process(context, prev) }
            }
        }
        return channelFlow { coroutineContext.withContext { next.invoke(context) } }
    }
}
