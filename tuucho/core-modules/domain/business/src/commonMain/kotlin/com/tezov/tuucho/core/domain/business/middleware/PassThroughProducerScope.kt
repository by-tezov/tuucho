package com.tezov.tuucho.core.domain.business.middleware

import kotlinx.coroutines.channels.ProducerScope

class PassThroughProducerScope<T : Any>(
    private val producerScope: ProducerScope<T>,
    private val onSendIntent: suspend (T) -> T
) : ProducerScope<T> by producerScope {
    override suspend fun send(
        element: T
    ) {
        producerScope.send(onSendIntent.invoke(element))
    }
}
