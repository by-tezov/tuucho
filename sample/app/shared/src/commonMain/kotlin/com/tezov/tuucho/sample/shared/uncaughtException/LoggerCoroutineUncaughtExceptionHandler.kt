package com.tezov.tuucho.sample.shared.uncaughtException

import com.tezov.tuucho.core.domain.business.protocol.CoroutineExceptionHandlerProtocol
import com.tezov.tuucho.core.domain.tool.protocol.SystemInformationProtocol
import com.tezov.tuucho.sample.shared._system.Logger

class LoggerCoroutineUncaughtExceptionHandler(
    private val logger: Logger,
    private val systemInformation: SystemInformationProtocol
) : CoroutineExceptionHandlerProtocol {

    override fun process(
        throwable: Throwable
    ): Throwable {
        logger.debug("THREAD") { systemInformation.currentThreadName() }
        logger.exception("UNCAUGHT", throwable) { "!!!" }
        return throwable // I want crash xD
    }
}
