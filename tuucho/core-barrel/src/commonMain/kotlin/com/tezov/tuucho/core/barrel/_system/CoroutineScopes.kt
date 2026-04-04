@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.barrel._system

import com.tezov.tuucho.core.domain.business._system.coroutine.CoroutineScope
import com.tezov.tuucho.core.domain.business.protocol.CoroutineExceptionMonitorProtocol
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopeProtocol
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Dispatchers.Unconfined
import kotlinx.coroutines.IO

class CoroutineScopes(
    exceptionMonitor: CoroutineExceptionMonitorProtocol?,
) : CoroutineScopesProtocol {
    override val unconfined: CoroutineScopeProtocol =
        CoroutineScope(
            name = "Unconfined",
            dispatcher = Unconfined,
            exceptionMonitor = exceptionMonitor,
        )
    override val default: CoroutineScopeProtocol =
        CoroutineScope(
            name = "Default",
            dispatcher = Default,
            exceptionMonitor = exceptionMonitor,
        )
    override val main: CoroutineScopeProtocol =
        CoroutineScope(
            name = "Main",
            dispatcher = Main,
            exceptionMonitor = exceptionMonitor,
        )
    override val io: CoroutineScopeProtocol =
        CoroutineScope(
            name = "IO",
            dispatcher = Dispatchers.IO,
            exceptionMonitor = exceptionMonitor,
        )

    override fun cancel() {
        unconfined.cancel()
        default.cancel()
        main.cancel()
        io.cancel()
    }
}
