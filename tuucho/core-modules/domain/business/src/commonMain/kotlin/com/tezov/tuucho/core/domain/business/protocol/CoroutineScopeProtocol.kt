package com.tezov.tuucho.core.domain.business.protocol

import com.tezov.tuucho.core.domain.tool.annotation.TuuchoInternalApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred

interface CoroutineScopeProtocol {
    val dispatcher: CoroutineDispatcher
    val scope: CoroutineScope

    suspend fun <T> withContext(
        block: suspend CoroutineScope.() -> T
    ): T

    fun <T> async(
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> T,
    ): Deferred<T>

    @TuuchoInternalApi
    fun <T> asyncOnCompletionThrowing(
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> T,
    ): Deferred<T>
}
