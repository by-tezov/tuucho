package com.tezov.tuucho.core.domain.tool.async

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlin.coroutines.CoroutineContext

interface CoroutineContextProtocol {
    val context: CoroutineContext
    val job: Job
    val scope: CoroutineScope

    fun <T> async(
        onException: ((e: Throwable) -> Unit)? = null,
        block: suspend CoroutineScope.() -> T,
    ): Deferred<T>

    suspend fun <T> await(block: suspend CoroutineScope.() -> T): T
}

open class CoroutineContext(
    name: String,
    override val context: CoroutineContext,
) : CoroutineContextProtocol {

    override val job: Job = SupervisorJob()

    override val scope: CoroutineScope = CoroutineScope(context + job + CoroutineName(name))

    override fun <T> async(
        onException: ((e: Throwable) -> Unit)?,
        block: suspend CoroutineScope.() -> T,
    ): Deferred<T> {
        val deferred = scope.async(block = block)
        deferred.invokeOnCompletion { e: Throwable? ->
            if (e != null) {
                onException?.invoke(e) ?: throw e
            }
        }
        return deferred
    }

    override suspend fun <T> await(block: suspend CoroutineScope.() -> T): T {
        return scope.async(block = block).await()
    }
}