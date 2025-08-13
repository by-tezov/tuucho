package com.tezov.tuucho.core.domain.tool.async

import kotlinx.coroutines.CoroutineExceptionHandler
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

    fun <T> async(block: suspend CoroutineScope.() -> T): Deferred<T>
    suspend fun <T> await(block: suspend CoroutineScope.() -> T): T
}

open class CoroutineContext(
    private val name: String,
    override val context: CoroutineContext,
    exceptionHandler: CoroutineExceptionHandler = CoroutineExceptionHandler { context, throwable ->
        println("Coroutine failed in $name: ${throwable.message}")
        throw throwable
    },
) : CoroutineContextProtocol {

    override val job: Job = SupervisorJob()

    override val scope: CoroutineScope = CoroutineScope(context + job + exceptionHandler)

    override fun <T> async(block: suspend CoroutineScope.() -> T): Deferred<T> {
        return scope.async(block = block)
    }

    override suspend fun <T> await(block: suspend CoroutineScope.() -> T): T {
        return async(block).await()
    }
}