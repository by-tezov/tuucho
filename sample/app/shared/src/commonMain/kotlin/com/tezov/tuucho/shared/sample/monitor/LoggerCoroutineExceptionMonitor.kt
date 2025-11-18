package com.tezov.tuucho.shared.sample.monitor

import com.tezov.tuucho.core.domain.tool.async.CoroutineExceptionMonitor
import com.tezov.tuucho.shared.sample._system.Logger
import kotlinx.coroutines.CoroutineName
import kotlin.coroutines.CoroutineContext

class LoggerCoroutineExceptionMonitor(
    private val logger: Logger
) : CoroutineExceptionMonitor {
    override fun process(context: CoroutineExceptionMonitor.Context
    ) {
        with(context) {
            logger.exception("COROUTINE", throwable) { "$id:$name" }
        }
    }
}
