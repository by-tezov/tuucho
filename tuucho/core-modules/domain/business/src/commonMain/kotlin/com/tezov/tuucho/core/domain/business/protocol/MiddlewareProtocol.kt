package com.tezov.tuucho.core.domain.business.protocol

import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow

fun interface MiddlewareProtocol<C, R> {
    fun interface Next<C, R> {
        suspend fun FlowCollector<R>.invoke(
            context: C
        )

        companion object {
            context(flowCollector: FlowCollector<R>)
            suspend fun <C, R> Next<C, R>?.invoke(
                context: C
            ) = this?.run { flowCollector.run { invoke(context) } }

            fun <C, R> Next<C, R>?.intercept(
                context: C
            ) = this?.run { flow { invoke(context) } }
        }
    }

    suspend fun FlowCollector<R>.process(
        context: C,
        next: Next<C, R>?
    )
}
