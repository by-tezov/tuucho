@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.barrel._system

import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.tool.async.CoroutineContext
import com.tezov.tuucho.core.domain.tool.async.CoroutineContextProtocol
import com.tezov.tuucho.core.domain.tool.async.CoroutineExceptionMonitor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

internal fun createCoroutineScopes(
    exceptionMonitor: CoroutineExceptionMonitor?
): CoroutineScopesProtocol = object : CoroutineScopesProtocol {
    override val database: CoroutineContextProtocol =
        CoroutineContext("Database", Dispatchers.IO, exceptionMonitor)
    override val network: CoroutineContextProtocol =
        CoroutineContext("Network", Dispatchers.IO, exceptionMonitor)
    override val parser: CoroutineContextProtocol =
        CoroutineContext("Parser", Dispatchers.Default, exceptionMonitor)
    override val renderer: CoroutineContextProtocol =
        CoroutineContext("Renderer", Dispatchers.Default, exceptionMonitor)
    override val navigation: CoroutineContextProtocol =
        CoroutineContext("Navigation", Dispatchers.Default, exceptionMonitor)
    override val useCase: CoroutineContextProtocol =
        CoroutineContext("UseCase", Dispatchers.Default, exceptionMonitor)
    override val action: CoroutineContextProtocol =
        CoroutineContext("Action", Dispatchers.Default, exceptionMonitor)
    override val event: CoroutineContextProtocol =
        CoroutineContext("Event", Dispatchers.Default, exceptionMonitor)
    override val default: CoroutineContextProtocol =
        CoroutineContext("Default", Dispatchers.Default, exceptionMonitor)
    override val main: CoroutineContextProtocol =
        CoroutineContext("Main", Dispatchers.Main, exceptionMonitor)
    override val io: CoroutineContextProtocol =
        CoroutineContext("IO", Dispatchers.IO, exceptionMonitor)
}
