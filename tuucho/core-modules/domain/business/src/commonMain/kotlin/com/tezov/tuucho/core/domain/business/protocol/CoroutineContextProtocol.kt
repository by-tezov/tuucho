package com.tezov.tuucho.core.domain.business.protocol

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.withContext

interface CoroutineContextProtocol {
    val dispatcher: CoroutineDispatcher
    val scope: CoroutineScope

    fun <T> async(
        throwOnFailure: Boolean,
        block: suspend CoroutineScope.() -> T,
    ): Deferred<T>

    suspend fun <T> withContext(
        block: suspend CoroutineScope.() -> T
    ): T

    fun <T> throwOnFailure(
        deferred: Deferred<T>
    )
}
