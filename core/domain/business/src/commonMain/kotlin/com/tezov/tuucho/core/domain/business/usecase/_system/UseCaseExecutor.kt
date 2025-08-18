package com.tezov.tuucho.core.domain.business.usecase._system

import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol

class UseCaseExecutor(
    private val coroutineScopes: CoroutineScopesProtocol,
) {

    fun <INPUT : Any, OUTPUT : Any> invoke(
        useCase: UseCaseProtocol<INPUT, OUTPUT>,
        input: INPUT,
        onException: ((DomainException) -> Unit)? = null,
        onResult: OUTPUT.() -> Unit = {},
    ) {
        coroutineScopes.useCase.async(
            onException = { e: Throwable ->
                val output = onException ?: throw e
                when (e) {
                    is DomainException -> output(e)
                    else -> output(DomainException.Unknown(e))
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
            })
    }

    suspend fun <INPUT : Any, OUTPUT : Any> invokeSuspend(
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

