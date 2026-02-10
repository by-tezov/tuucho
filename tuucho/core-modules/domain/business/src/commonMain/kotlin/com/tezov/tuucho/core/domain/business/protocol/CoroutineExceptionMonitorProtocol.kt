package com.tezov.tuucho.core.domain.business.protocol

interface CoroutineExceptionMonitorProtocol {
    data class Context(
        val name: String,
        val throwable: Throwable
    )

    suspend fun process(
        context: Context
    )
}
