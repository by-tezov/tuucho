@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.domain.business.usecase._system

import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol

class UseCaseExecutor(
    private val coroutineScopes: CoroutineScopesProtocol,
) {
    fun <INPUT : Any, OUTPUT : Any> async(
        useCase: UseCaseProtocol<INPUT, OUTPUT>,
        input: INPUT,
        onException: ((DomainException) -> Unit)? = null,
        onResult: OUTPUT.() -> Unit = {},
    ) {
        coroutineScopes.useCase.async(
            onException = { throwable ->
                val output = onException ?: throw throwable
                when (throwable) {
                    is DomainException -> output(throwable)
                    else -> output(DomainException.Unknown(throwable))
                }
            },
            block = {
                when (useCase) {
                    is UseCaseProtocol.Async<INPUT, OUTPUT> -> useCase.invoke(input).also {
                        it.onResult()
                    }

                    is UseCaseProtocol.Sync<INPUT, OUTPUT> -> useCase.invoke(input).also {
                        it.onResult()
                    }
                }
            }
        )
    }

    suspend fun <INPUT : Any, OUTPUT : Any> await(
        useCase: UseCaseProtocol<INPUT, OUTPUT>,
        input: INPUT,
    ): OUTPUT {
        try {
            return coroutineScopes.useCase.await {
                when (useCase) {
                    is UseCaseProtocol.Async<INPUT, OUTPUT> -> useCase.invoke(input)
                    is UseCaseProtocol.Sync<INPUT, OUTPUT> -> useCase.invoke(input)
                }
            }
        } catch (e: Throwable) {
            throw when (e) {
                is DomainException -> e
                else -> DomainException.Unknown(e)
            }
        }
    }
}
