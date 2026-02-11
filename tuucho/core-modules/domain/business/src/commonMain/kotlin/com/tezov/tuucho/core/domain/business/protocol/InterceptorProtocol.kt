package com.tezov.tuucho.core.domain.business.protocol

interface InterceptorProtocol<C> {

    suspend fun process(
        context: C,
        exception: Throwable,
        replay: suspend () -> Unit
    )
}
