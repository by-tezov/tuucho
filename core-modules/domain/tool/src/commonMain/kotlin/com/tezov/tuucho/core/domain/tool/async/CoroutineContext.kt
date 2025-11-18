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
    val supervisorJob: Job
    val scope: CoroutineScope

    fun <T> async(
        onException: ((e: Throwable) -> Unit)? = null,
        block: suspend CoroutineScope.() -> T,
    ): Deferred<T>

    suspend fun <T> await(
        block: suspend CoroutineScope.() -> T
    ): T
}

class CoroutineContext(
    name: String,
    override val context: CoroutineContext,
    private val exceptionMonitor: CoroutineExceptionMonitor?
) : CoroutineContextProtocol {
    override val supervisorJob: Job = SupervisorJob()

    override val scope: CoroutineScope = CoroutineScope(context + supervisorJob + CoroutineName(name))

    override fun <T> async(
        onException: ((e: Throwable) -> Unit)?,
        block: suspend CoroutineScope.() -> T,
    ): Deferred<T> {
        val deferred = scope.async(block = block)
        deferred.invokeOnCompletion { throwable ->
            throwable?.let {
                exceptionMonitor?.process(
                    context = CoroutineExceptionMonitor.Context(
                        name = scope.coroutineContext[CoroutineName]?.name ?: "unknown",
                        id = deferred.hashCode().toHexString(),
                        throwable = throwable
                    )
                )
                onException?.invoke(throwable) ?: throw throwable
            }
        }
        return deferred
    }

    override suspend fun <T> await(
        block: suspend CoroutineScope.() -> T
    ): T = scope
        .async(block = block)
        .also { deferred ->
            exceptionMonitor?.let {
                deferred.invokeOnCompletion { throwable ->
                    throwable?.let {
                        exceptionMonitor.process(
                            context = CoroutineExceptionMonitor.Context(
                                name = scope.coroutineContext[CoroutineName]?.name ?: "unknown",
                                id = deferred.hashCode().toHexString(),
                                throwable = throwable
                            )
                        )
                    }
                }
            }
        }.await()
}
