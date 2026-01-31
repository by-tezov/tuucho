package com.tezov.tuucho.core.domain.business.protocol

interface CoroutineExceptionHandlerProtocol {
    fun process(
        throwable: Throwable
    ): Throwable?
}
