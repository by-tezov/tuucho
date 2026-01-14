package com.tezov.tuucho.sample.shared.monitor

import com.tezov.tuucho.core.domain.tool.async.CoroutineExceptionMonitor
import com.tezov.tuucho.core.domain.tool.protocol.SystemInformationProtocol
import com.tezov.tuucho.sample.shared._system.Logger

class LoggerCoroutineExceptionMonitor(
    private val logger: Logger,
    private val systemInformation: SystemInformationProtocol
) : CoroutineExceptionMonitor {
    override fun process(
        context: CoroutineExceptionMonitor.Context
    ) {
        with(context) {
            logger.debug("THREAD") { systemInformation.currentThreadName() }
            logger.exception("COROUTINE", throwable) { "$id:$name" }
        }
    }
}
