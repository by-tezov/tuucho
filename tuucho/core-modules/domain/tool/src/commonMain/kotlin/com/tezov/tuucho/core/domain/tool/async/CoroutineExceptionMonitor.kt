package com.tezov.tuucho.core.domain.tool.async

interface CoroutineExceptionMonitor {
    data class Context(
        val name: String,
        val id: String,
        val throwable: Throwable
    )

    fun process(
        context: Context
    )
}
