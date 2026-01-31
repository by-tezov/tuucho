package com.tezov.tuucho.core.domain.business.protocol

interface CoroutineExceptionMonitorProtocol {
    data class Context(
        val name: String,
        val id: String,
        val throwable: Throwable
    )

    fun process(
        context: Context
    )
}
