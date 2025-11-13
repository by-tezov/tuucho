package com.tezov.tuucho.shared.sample.monitor

import com.tezov.tuucho.core.domain.tool.async.CoroutineExceptionMonitorProtocol
import com.tezov.tuucho.shared.sample._system.Logger
import kotlinx.coroutines.CoroutineName
import kotlin.coroutines.CoroutineContext

class LoggerCoroutineExceptionMonitor(
    private val logger: Logger
) : CoroutineExceptionMonitorProtocol {
    override fun process(context: CoroutineContext, throwable: Throwable) {
        logger.exception("COROUTINE", throwable) { "${context[CoroutineName]?.name}" }
    }
}
