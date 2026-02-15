package com.tezov.tuucho.sample.shared.exceptionHandler

import com.tezov.tuucho.core.domain.business.interaction.exceptionHandler.ShadowerExceptionHandler
import com.tezov.tuucho.sample.shared._system.Config
import kotlinx.coroutines.delay

class ShadowerNavigateExceptionHandler : ShadowerExceptionHandler.Navigate {

    override suspend fun process(
        context: ShadowerExceptionHandler.Navigate.Context,
        exception: Throwable,
        replay: suspend () -> Unit
    ) {
        val maxRetries = 3
        var failure: Throwable? = exception
        for (attempt in 0 until maxRetries) {
            val delayMs =
                (Config.networkMinRetryDelay * (1 shl attempt)).coerceAtMost(Config.networkMaxRetryDelay)
            delay(delayMs)
            val result = runCatching { replay.invoke() }
            failure = result.exceptionOrNull()
            if (failure == null) {
                break
            }
        }

        // Shadower failed to retrieve remote data at all attempt,
        // TODO: Do something better than forwarding the failure
        // - have a way to inform view the failure (all skimmer)
        failure?.let { throw failure }
    }
}