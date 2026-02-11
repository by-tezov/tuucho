package com.tezov.tuucho.core.domain.business.protocol

import com.tezov.tuucho.core.domain.business.interaction.middleware.PassThroughProducerScope
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.flow.channelFlow

interface MiddlewareProtocol<C> {
    fun interface Next<C> {
        suspend fun invoke(
            context: C
        )
    }

    suspend fun process(
        context: C,
        next: Next<C>?
    )
}

interface MiddlewareProtocolWithReturn<C, R : Any> {
    fun interface Next<C, R : Any> {
        suspend fun ProducerScope<R>.invoke(
            context: C
        )

        companion object {
            context(producerScope: ProducerScope<R>)
            suspend fun <C, R : Any> Next<C, R>.invoke(
                context: C
            ) = producerScope.run { invoke(context) }

            suspend fun <C, R : Any> ProducerScope<R>.passThrough(
                next: Next<C, R>?,
                context: C,
                onSendIntent: suspend (R) -> R
            ) = PassThroughProducerScope(this, onSendIntent).also {
                it.run { next?.invoke(context) }
            }

            fun <C, R : Any> Next<C, R>.intercept(
                context: C
            ) = channelFlow {
                runCatching { invoke(context) }
                    .onFailure { close(it) }
                    .onSuccess { close() }
            }
        }
    }

    suspend fun ProducerScope<R>.process(
        context: C,
        next: Next<C, R>?
    )
}
