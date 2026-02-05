package com.tezov.tuucho.sample.shared.monitor

import com.tezov.tuucho.core.domain.business.protocol.CoroutineExceptionMonitorProtocol
import com.tezov.tuucho.sample.shared._system.Logger

class LoggerCoroutineExceptionMonitor(
    private val logger: Logger,
) : CoroutineExceptionMonitorProtocol {
    override suspend fun process(
        context: CoroutineExceptionMonitorProtocol.Context
    ) {
        with(context) {
            logger.thread()
            logger.exception("MON-COROUTINE", throwable) { "$id:$name" }
        }
    }
}
