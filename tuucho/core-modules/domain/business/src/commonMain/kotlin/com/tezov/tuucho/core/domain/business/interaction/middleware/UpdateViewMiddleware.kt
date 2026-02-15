package com.tezov.tuucho.core.domain.business.interaction.middleware

import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.UpdateViewUseCase

fun interface UpdateViewMiddleware : MiddlewareProtocol<UpdateViewMiddleware.Context> {
    data class Context(
        val input: UpdateViewUseCase.Input,
    )
}
