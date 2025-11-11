package com.tezov.tuucho.shared.sample.middleware

import com.tezov.tuucho.core.domain.business.middleware.NavigationMiddleware
import com.tezov.tuucho.core.domain.business.middleware.NextMiddleware
import com.tezov.tuucho.shared.sample._system.Config
import com.tezov.tuucho.shared.sample._system.Page
import kotlinx.coroutines.delay

class ExceptionNavigateToUrlMiddleware : NavigationMiddleware.ToUrl {

    override suspend fun process(
        context: NavigationMiddleware.ToUrl.Context,
        next: NextMiddleware<NavigationMiddleware.ToUrl.Context>,
    ) {
        processWithRetry(
            context = context,
            next = next,
            attempt = 0,
            maxRetries = 3
        )
    }

    private suspend fun processWithRetry(
        context: NavigationMiddleware.ToUrl.Context,
        next: NextMiddleware<NavigationMiddleware.ToUrl.Context>,
        attempt: Int,
        maxRetries: Int,
    ) {
        try {
            next.invoke(context)
        } catch (exception: Throwable) {
            //IMPROVE: check exception and design action in accord with exception
            if ((attempt + 1) < maxRetries) {
                val delayMs = (Config.networkMinRetryDelay * (1 shl attempt)).coerceAtMost(Config.networkMaxRetryDelay)
                delay(delayMs)
                processWithRetry(context, next, attempt + 1, maxRetries)
            } else if (context.input.url != Page.failSafe) {
                next.invoke(
                    context.copy(
                        input = context.input.copy(url = Page.failSafe)
                    )
                )
            } else {
                throw exception // crash application, should never happen because FailSafe can not fail
            }
        }
    }
}
