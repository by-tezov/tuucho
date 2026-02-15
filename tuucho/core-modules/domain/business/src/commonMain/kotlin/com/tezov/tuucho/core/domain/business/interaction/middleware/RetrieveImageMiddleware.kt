package com.tezov.tuucho.core.domain.business.interaction.middleware

import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocolWithReturn
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.RetrieveImageUseCase

fun interface RetrieveImageMiddleware<S : Any> :
    MiddlewareProtocolWithReturn<RetrieveImageMiddleware.Context, RetrieveImageUseCase.Output<S>> {
    data class Context(
        val input: RetrieveImageUseCase.Input,
    )
}
