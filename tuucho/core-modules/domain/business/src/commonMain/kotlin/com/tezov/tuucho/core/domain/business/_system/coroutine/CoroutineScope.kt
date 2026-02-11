@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.domain.business._system.coroutine

import com.tezov.tuucho.core.domain.business.protocol.CoroutineExceptionMonitorProtocol
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopeProtocol
import com.tezov.tuucho.core.domain.tool.annotation.TuuchoInternalApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException

class CoroutineScope(
    name: String,
    override val dispatcher: CoroutineDispatcher,
    private val exceptionMonitor: CoroutineExceptionMonitorProtocol?
) : CoroutineScopeProtocol {
    private val supervisorJob: Job = SupervisorJob()

    override val scope: CoroutineScope = CoroutineScope(
        dispatcher + supervisorJob + CoroutineName(name)
    )

    override suspend fun <T> withContext(
        block: suspend CoroutineScope.() -> T
    ): T = withContext(dispatcher) {
        runCatching { block() }.getOrElse { throwable ->
            exceptionMonitor?.log(throwable)
            throw throwable
        }
    }

    override fun <T> async(
        start: CoroutineStart,
        block: suspend CoroutineScope.() -> T,
    ) = scope
        .async(block = block)
        .also { deferred ->
            exceptionMonitor?.let { monitor ->
                deferred.invokeOnCompletion {
                    it?.let { throwable ->
                        scope.async { monitor.log(throwable) }
                    }
                }
            }
        }

    @TuuchoInternalApi
    override fun <T> asyncOnCompletionThrowing(
        start: CoroutineStart,
        block: suspend CoroutineScope.() -> T,
    ) = scope
        .async(block = block)
        .also { deferred ->
            deferred.invokeOnCompletion {
                it?.let { throwable ->
                    exceptionMonitor?.let { monitor ->
                        scope.async { monitor.log(throwable) }
                    }
                    if (throwable is CancellationException) return@invokeOnCompletion
                    throw throwable
                }
            }
        }

    private suspend fun CoroutineExceptionMonitorProtocol.log(
        throwable: Throwable
    ) {
        process(
            context = CoroutineExceptionMonitorProtocol.Context(
                name = scope.coroutineContext[CoroutineName]?.name ?: "unknown",
                throwable = throwable
            )
        )
    }

    override fun cancel() {
        scope.cancel()
    }
}
