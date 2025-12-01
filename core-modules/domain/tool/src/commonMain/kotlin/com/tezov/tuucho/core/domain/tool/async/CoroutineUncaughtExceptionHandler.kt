package com.tezov.tuucho.core.domain.tool.async

interface CoroutineUncaughtExceptionHandler {
    fun process(
        throwable: Throwable
    ): Throwable?
}
