package com.tezov.tuucho.core.domain.business.protocol

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

interface CoroutineContextProtocol {
    val context: CoroutineContext
    val scope: CoroutineScope

    fun <T> async(
        throwOnFailure: Boolean,
        block: suspend CoroutineScope.() -> T,
    ): Deferred<T>

    suspend fun <T> withContext(
        block: suspend CoroutineScope.() -> T
    ): T = withContext(context) {
        async(throwOnFailure = false, block = block).await()
    }

    fun <T> throwOnFailure(
        deferred: Deferred<T>
    )
}
