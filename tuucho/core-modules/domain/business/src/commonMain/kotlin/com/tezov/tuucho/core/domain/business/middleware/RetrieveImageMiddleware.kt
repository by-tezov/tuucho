package com.tezov.tuucho.core.domain.business.middleware

import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.RetrieveImageUseCase
import kotlinx.coroutines.flow.Flow

fun interface RetrieveImageMiddleware<S : Any> : MiddlewareProtocol<RetrieveImageMiddleware.Context, Flow<RetrieveImageUseCase.Output<S>>> {
    data class Context(
        val input: RetrieveImageUseCase.Input,
    )
}
