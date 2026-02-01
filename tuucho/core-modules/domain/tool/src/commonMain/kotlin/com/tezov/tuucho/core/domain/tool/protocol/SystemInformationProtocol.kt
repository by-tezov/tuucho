package com.tezov.tuucho.core.domain.tool.protocol

import kotlinx.coroutines.currentCoroutineContext
import kotlin.coroutines.ContinuationInterceptor

interface SystemInformationProtocol {
    suspend fun currentDispatcher() = currentCoroutineContext()[ContinuationInterceptor]
    fun currentThreadName(): String
}
