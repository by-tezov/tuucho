package com.tezov.tuucho.core.domain.tool.async

import kotlin.coroutines.CoroutineContext

interface CoroutineExceptionMonitorProtocol {
    fun process(context: CoroutineContext, throwable: Throwable)
}
