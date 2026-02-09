package com.tezov.tuucho.core.domain.business.protocol

import com.tezov.tuucho.core.domain.tool.annotation.TuuchoInternalApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred

interface CoroutineScopeProtocol {
    val dispatcher: CoroutineDispatcher
    val scope: CoroutineScope

    fun <T> async(
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> T,
    ): Deferred<T>

    suspend fun <T> withContext(
        block: suspend CoroutineScope.() -> T
    ): T

    @TuuchoInternalApi
    fun <T> asyncOnCompletionThrowing(
        block: suspend CoroutineScope.() -> T,
    ): Deferred<T>

    @TuuchoInternalApi
    fun <T> throwOnFailure(
        deferred: Deferred<T>
    )
}
