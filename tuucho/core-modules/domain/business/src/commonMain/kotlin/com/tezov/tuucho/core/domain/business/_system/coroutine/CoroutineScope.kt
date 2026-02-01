@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.domain.business._system.coroutine

import com.tezov.tuucho.core.domain.business.protocol.CoroutineExceptionMonitorProtocol
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopeProtocol
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

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
            exceptionMonitor?.process(
                context = CoroutineExceptionMonitorProtocol.Context(
                    name = scope.coroutineContext[CoroutineName]?.name ?: "unknown",
                    id = block.hashCode().toHexString(),
                    throwable = throwable
                )
            )
            throw throwable
        }
    }

    override fun <T> async(
        throwOnFailure: Boolean,
        block: suspend CoroutineScope.() -> T,
    ) = scope.async(block = block)
        .also { deferred ->
            if (throwOnFailure) {
                throwOnFailure(deferred)
            }
            exceptionMonitor?.attach(deferred)
        }

    override fun <T> throwOnFailure(
        deferred: Deferred<T>
    ) {
        deferred.invokeOnCompletion { throwable ->
            throwable ?: return@invokeOnCompletion
            throw throwable
        }
    }

    private fun <T> CoroutineExceptionMonitorProtocol.attach(
        deferred: Deferred<T>
    ) {
        deferred.invokeOnCompletion { throwable ->
            throwable?.let {
                scope.async {
                    process(
                        context = CoroutineExceptionMonitorProtocol.Context(
                            name = scope.coroutineContext[CoroutineName]?.name ?: "unknown",
                            id = deferred.hashCode().toHexString(),
                            throwable = throwable
                        )
                    )
                }
            }
        }
    }
}
