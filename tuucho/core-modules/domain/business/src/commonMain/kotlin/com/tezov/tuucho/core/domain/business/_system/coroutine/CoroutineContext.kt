@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.domain.business._system.coroutine

import com.tezov.tuucho.core.domain.business.protocol.CoroutineContextProtocol
import com.tezov.tuucho.core.domain.business.protocol.CoroutineExceptionHandlerProtocol
import com.tezov.tuucho.core.domain.business.protocol.CoroutineExceptionMonitorProtocol
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class CoroutineContext(
    name: String,
    override val context: CoroutineContext,
    private val exceptionMonitor: CoroutineExceptionMonitorProtocol?,
    private val uncaughtExceptionHandler: CoroutineExceptionHandlerProtocol?
) : CoroutineContextProtocol {
    private val supervisorJob: Job = SupervisorJob()

    override val scope: CoroutineScope = CoroutineScope(context + supervisorJob + CoroutineName(name))

    override suspend fun <T> withContext(
        block: suspend CoroutineScope.() -> T
    ): T = withContext(context) { block() }

    override fun <T> async(
        throwOnFailure: Boolean,
        block: suspend CoroutineScope.() -> T,
    ) = scope.async(block = block)
        .also { deferred ->
            if (throwOnFailure) {
                throwOnFailure(deferred)
            }
            exceptionMonitor?.let {
                deferred.invokeOnCompletion { throwable ->
                    throwable?.let {
                        exceptionMonitor.process(
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

    override fun <T> throwOnFailure(
        deferred: Deferred<T>
    ) {
        deferred.invokeOnCompletion { throwable ->
            throwable ?: return@invokeOnCompletion
            uncaughtExceptionHandler?.let { handler ->
                handler.process(throwable)?.let { throw it } ?: Unit
            } ?: throw throwable
        }
    }
}
