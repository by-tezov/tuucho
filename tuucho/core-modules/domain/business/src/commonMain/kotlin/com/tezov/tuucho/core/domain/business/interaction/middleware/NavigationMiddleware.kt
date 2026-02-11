package com.tezov.tuucho.core.domain.business.interaction.middleware

import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.NavigateToUrlUseCase

object NavigationMiddleware {
    fun interface ToUrl : MiddlewareProtocol<ToUrl.Context> {
        data class Context(
            val currentUrl: String?,
            val input: NavigateToUrlUseCase.Input,
        )
    }

    fun interface Back : MiddlewareProtocol<Back.Context> {
        data class Context(
            val currentUrl: String,
            val nextUrl: String?,
        )
    }

    fun interface Finish : MiddlewareProtocol<Unit>
}
