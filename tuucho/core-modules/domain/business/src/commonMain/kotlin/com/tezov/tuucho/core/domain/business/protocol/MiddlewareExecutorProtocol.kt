package com.tezov.tuucho.core.domain.business.protocol

import com.tezov.tuucho.core.domain.tool.async.CoroutineContextProtocol
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.shareIn

interface MiddlewareExecutorProtocol {
    suspend fun <C, R> FlowCollector<R>.process(
        middlewares: List<MiddlewareProtocol<C, R>>,
        context: C
    )

    companion object {

        fun <C, R> MiddlewareExecutorProtocol.asFlow(
            middlewares: List<MiddlewareProtocol<C, R>>,
            context: C
        ): Flow<R> {
            return run {
                flow {
                    process(middlewares, context)
                }
            }
        }

        fun <T> Flow<T>.asHotFlow(
            coroutineContext: CoroutineContextProtocol
        ) = shareIn(
            scope = coroutineContext.scope,
            started = SharingStarted.Eagerly,
            replay = 1
        )

    }
}
