package com.tezov.tuucho.core.domain.business.protocol

import kotlinx.coroutines.flow.FlowCollector

fun interface MiddlewareProtocol<C, R> {
    fun interface Next<C, R> {
        suspend fun invoke(
            context: C
        )
    }

    suspend fun FlowCollector<R>.process(
        context: C,
        next: Next<C, R>?
    )
}
