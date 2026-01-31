@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.barrel._system

import com.tezov.tuucho.core.domain.business._system.coroutine.CoroutineContext
import com.tezov.tuucho.core.domain.business.protocol.CoroutineContextProtocol
import com.tezov.tuucho.core.domain.business.protocol.CoroutineExceptionHandlerProtocol
import com.tezov.tuucho.core.domain.business.protocol.CoroutineExceptionMonitorProtocol
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import kotlinx.coroutines.Dispatchers

internal class CoroutineScopes(
    exceptionMonitor: CoroutineExceptionMonitorProtocol?,
    uncaughtExceptionHandler: CoroutineExceptionHandlerProtocol?
) : CoroutineScopesProtocol {
    override val unconfined: CoroutineContextProtocol =
        CoroutineContext(
            name = "Unconfined",
            context = Dispatchers.Unconfined,
            exceptionMonitor = exceptionMonitor,
            uncaughtExceptionHandler = uncaughtExceptionHandler
        )
    override val default: CoroutineContextProtocol =
        CoroutineContext(
            name = "Default",
            context = Dispatchers.Default,
            exceptionMonitor = exceptionMonitor,
            uncaughtExceptionHandler = uncaughtExceptionHandler
        )
    override val main: CoroutineContextProtocol =
        CoroutineContext(
            name = "Main",
            context = Dispatchers.Main,
            exceptionMonitor = exceptionMonitor,
            uncaughtExceptionHandler = uncaughtExceptionHandler
        )
    override val io: CoroutineContextProtocol =
        CoroutineContext(
            name = "IO",
            context = Dispatchers.IO,
            exceptionMonitor = exceptionMonitor,
            uncaughtExceptionHandler = uncaughtExceptionHandler
        )
}
