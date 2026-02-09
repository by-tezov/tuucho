//package com.tezov.tuucho.sample.shared.middleware.navigateToUrl
//
//import com.tezov.tuucho.core.domain.business.middleware.NavigationMiddleware
//import com.tezov.tuucho.sample.shared._system.Config
//import kotlinx.coroutines.delay
//
//class OnShadowerException : NavigationMiddleware.ToUrl.OnShadowerException {
//    override suspend fun process(
//        exception: Throwable,
//        context: NavigationMiddleware.ToUrl.Context,
//        replay: suspend () -> Unit
//    ) {
//        val maxRetries = 3
//        var failure: Throwable? = exception
//        for (attempt in 0 until maxRetries) {
//            val delayMs =
//                (Config.networkMinRetryDelay * (1 shl attempt)).coerceAtMost(Config.networkMaxRetryDelay)
//            delay(delayMs)
//            val result = runCatching { replay.invoke() }
//            failure = result.exceptionOrNull()
//            if (failure == null) {
//                break
//            }
//        }
//
//
//        // Shadower failed to retrieve remote data at all attempt, we crash the application
//        // TODO: find a design to allow to not crash application but
//        // - have a way to inform view the failure (all skimmer)
//        // - on back, when it was failure, how to attempt reload the block if back shadower is off ?
//        failure?.let { throw failure }
//    }
//}
