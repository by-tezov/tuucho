package com.tezov.tuucho.shared.sample.middleware

import com.tezov.tuucho.core.domain.business.middleware.NavigationMiddleware
import com.tezov.tuucho.core.domain.business.middleware.NextMiddleware
import kotlinx.coroutines.delay

class ExceptionNavigateToUrlMiddleware : NavigationMiddleware.ToUrl {

    override suspend fun process(
        context: NavigationMiddleware.ToUrl.Context,
        next: NextMiddleware<NavigationMiddleware.ToUrl.Context>?,
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
        next: NextMiddleware<NavigationMiddleware.ToUrl.Context>?,
        attempt: Int,
        maxRetries: Int,
    ) {
        try {
            next?.invoke(context)
        } catch (exception: Throwable) {

            println(exception)

            //TODO: check exception and design action in accord with exception
            if (attempt < maxRetries) {
                val delayMs = (1000L * (1 shl attempt)).coerceAtMost(5000L)
                delay(delayMs)
                processWithRetry(context, next, attempt + 1, maxRetries)
            } else {
                //TODO: redirect to embedded user helper application page instead of crashing the app
                throw exception
            }
        }
    }
}
