@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.barrel._system

import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.tool.async.CoroutineContext
import com.tezov.tuucho.core.domain.tool.async.CoroutineContextProtocol
import com.tezov.tuucho.core.domain.tool.async.CoroutineExceptionMonitor
import com.tezov.tuucho.core.domain.tool.async.CoroutineUncaughtExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

internal class CoroutineScopes(
    exceptionMonitor: CoroutineExceptionMonitor?,
    uncaughtExceptionHandler: CoroutineUncaughtExceptionHandler?
) : CoroutineScopesProtocol {
    override val database: CoroutineContextProtocol =
        CoroutineContext(
            name = "Database",
            context = Dispatchers.IO,
            exceptionMonitor = exceptionMonitor,
            uncaughtExceptionHandler = uncaughtExceptionHandler
        )
    override val network: CoroutineContextProtocol =
        CoroutineContext(
            name = "Network",
            context = Dispatchers.IO,
            exceptionMonitor = exceptionMonitor,
            uncaughtExceptionHandler = uncaughtExceptionHandler
        )
    override val parser: CoroutineContextProtocol =
        CoroutineContext(
            name = "Parser",
            context = Dispatchers.Default,
            exceptionMonitor = exceptionMonitor,
            uncaughtExceptionHandler = uncaughtExceptionHandler
        )
    override val renderer: CoroutineContextProtocol =
        CoroutineContext(
            name = "Renderer",
            context = Dispatchers.Default,
            exceptionMonitor = exceptionMonitor,
            uncaughtExceptionHandler = uncaughtExceptionHandler
        )
    override val navigation: CoroutineContextProtocol =
        CoroutineContext(
            name = "Navigation",
            context = Dispatchers.Default,
            exceptionMonitor = exceptionMonitor,
            uncaughtExceptionHandler = uncaughtExceptionHandler
        )
    override val useCase: CoroutineContextProtocol =
        CoroutineContext(
            name = "UseCase",
            context = Dispatchers.Default,
            exceptionMonitor = exceptionMonitor,
            uncaughtExceptionHandler = uncaughtExceptionHandler
        )
    override val action: CoroutineContextProtocol =
        CoroutineContext(
            name = "Action",
            context = Dispatchers.Default,
            exceptionMonitor = exceptionMonitor,
            uncaughtExceptionHandler = uncaughtExceptionHandler
        )
    override val event: CoroutineContextProtocol =
        CoroutineContext(
            name = "Event",
            context = Dispatchers.Default,
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
