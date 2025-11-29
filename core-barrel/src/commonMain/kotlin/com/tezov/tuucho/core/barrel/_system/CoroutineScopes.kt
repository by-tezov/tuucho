@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.barrel._system

import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.tool.async.CoroutineContextProtocol
import com.tezov.tuucho.core.domain.tool.async.CoroutineExceptionMonitor
import kotlinx.coroutines.Dispatchers

internal fun createCoroutineScopes(
    exceptionMonitor: CoroutineExceptionMonitor?,
): CoroutineScopesProtocol = object : CoroutineScopesProtocol {
    override val database: CoroutineContextProtocol =
        _root_ide_package_.com.tezov.tuucho.core.domain.tool.async.CoroutineContext(
            "Database",
            Dispatchers.IO,
            exceptionMonitor
        )
    override val network: CoroutineContextProtocol =
        _root_ide_package_.com.tezov.tuucho.core.domain.tool.async.CoroutineContext(
            "Network",
            Dispatchers.IO,
            exceptionMonitor
        )
    override val parser: CoroutineContextProtocol =
        _root_ide_package_.com.tezov.tuucho.core.domain.tool.async.CoroutineContext(
            "Parser",
            Dispatchers.Default,
            exceptionMonitor
        )
    override val renderer: CoroutineContextProtocol =
        _root_ide_package_.com.tezov.tuucho.core.domain.tool.async.CoroutineContext(
            "Renderer",
            Dispatchers.Default,
            exceptionMonitor
        )
    override val navigation: CoroutineContextProtocol =
        _root_ide_package_.com.tezov.tuucho.core.domain.tool.async.CoroutineContext(
            "Navigation",
            Dispatchers.Default,
            exceptionMonitor
        )
    override val useCase: CoroutineContextProtocol =
        _root_ide_package_.com.tezov.tuucho.core.domain.tool.async.CoroutineContext(
            "UseCase",
            Dispatchers.Default,
            exceptionMonitor
        )
    override val action: CoroutineContextProtocol =
        _root_ide_package_.com.tezov.tuucho.core.domain.tool.async.CoroutineContext(
            "Action",
            Dispatchers.Default,
            exceptionMonitor
        )
    override val event: CoroutineContextProtocol =
        _root_ide_package_.com.tezov.tuucho.core.domain.tool.async.CoroutineContext(
            "Event",
            Dispatchers.Default,
            exceptionMonitor
        )
    override val default: CoroutineContextProtocol =
        _root_ide_package_.com.tezov.tuucho.core.domain.tool.async.CoroutineContext(
            "Default",
            Dispatchers.Default,
            exceptionMonitor
        )
    override val main: CoroutineContextProtocol =
        _root_ide_package_.com.tezov.tuucho.core.domain.tool.async.CoroutineContext(
            "Main",
            Dispatchers.Main,
            exceptionMonitor
        )
    override val io: CoroutineContextProtocol =
        _root_ide_package_.com.tezov.tuucho.core.domain.tool.async.CoroutineContext(
            "IO",
            Dispatchers.IO,
            exceptionMonitor
        )
}
