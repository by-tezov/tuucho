package com.tezov.tuucho.core.domain.business.interaction.middleware

import com.tezov.tuucho.core.domain.business.interaction.middleware.SendDataMiddleware.Context
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocolWithReturn
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.SendDataUseCase

fun interface SendDataMiddleware : MiddlewareProtocolWithReturn<Context, SendDataUseCase.Output> {
    data class Context(
        val input: SendDataUseCase.Input,
    )
}
