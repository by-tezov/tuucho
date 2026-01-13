package com.tezov.tuucho.core.domain.business.middleware

import com.tezov.tuucho.core.domain.business.middleware.SendDataMiddleware.Context
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.SendDataUseCase

fun interface SendDataMiddleware : MiddlewareProtocol<Context, SendDataUseCase.Output> {
    data class Context(
        val input: SendDataUseCase.Input,
    )
}
