package com.tezov.tuucho.core.domain.business.middleware

import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.NavigateToUrlUseCase

object NavigationMiddleware {
    fun interface ToUrl : MiddlewareProtocol<ToUrl.Context, Unit> {
        fun interface OnShadowerException {
            suspend fun process(
                exception: Throwable,
                context: Context,
                replay: suspend () -> Unit
            )
        }

        data class Context(
            val currentUrl: String?,
            val input: NavigateToUrlUseCase.Input,
            val onShadowerException: OnShadowerException?
        )
    }

    fun interface Back : MiddlewareProtocol<Back.Context, Unit> {
        fun interface OnShadowerException {
            suspend fun process(
                exception: Throwable,
                context: Context,
                replay: suspend () -> Unit
            )
        }

        data class Context(
            val currentUrl: String,
            val nextUrl: String?,
            val onShadowerException: OnShadowerException?
        )
    }
}
