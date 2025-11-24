package com.tezov.tuucho.core.domain.business.middleware

import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.NavigateToUrlUseCase

object NavigationMiddleware {
    fun interface ToUrl : MiddlewareProtocol<ToUrl.Context, Unit> {
        data class Context(
            val currentUrl: String?,
            val input: NavigateToUrlUseCase.Input,
            val onShadowerException: (suspend (exception: Throwable, context: Context, replay: suspend () -> Unit) -> Unit)?
        )
    }

    fun interface Back : MiddlewareProtocol<Back.Context, Unit> {
        data class Context(
            val currentUrl: String,
            val nextUrl: String?,
            val onShadowerException: ((exception: Throwable, context: Context, replay: suspend () -> Unit) -> Unit)?
        )
    }
}
