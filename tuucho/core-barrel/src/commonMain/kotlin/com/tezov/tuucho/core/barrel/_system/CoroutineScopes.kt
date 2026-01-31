@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.barrel._system

import com.tezov.tuucho.core.domain.business._system.coroutine.CoroutineContext
import com.tezov.tuucho.core.domain.business.protocol.CoroutineContextProtocol
import com.tezov.tuucho.core.domain.business.protocol.CoroutineExceptionMonitorProtocol
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import kotlinx.coroutines.Dispatchers

internal class CoroutineScopes(
    exceptionMonitor: CoroutineExceptionMonitorProtocol?,
) : CoroutineScopesProtocol {
    override val unconfined: CoroutineContextProtocol =
        CoroutineContext(
            name = "Unconfined",
            dispatcher = Dispatchers.Unconfined,
            exceptionMonitor = exceptionMonitor,
        )
    override val default: CoroutineContextProtocol =
        CoroutineContext(
            name = "Default",
            dispatcher = Dispatchers.Default,
            exceptionMonitor = exceptionMonitor,
        )
    override val main: CoroutineContextProtocol =
        CoroutineContext(
            name = "Main",
            dispatcher = Dispatchers.Main,
            exceptionMonitor = exceptionMonitor,
        )
    override val io: CoroutineContextProtocol =
        CoroutineContext(
            name = "IO",
            dispatcher = Dispatchers.IO,
            exceptionMonitor = exceptionMonitor,
        )
}
