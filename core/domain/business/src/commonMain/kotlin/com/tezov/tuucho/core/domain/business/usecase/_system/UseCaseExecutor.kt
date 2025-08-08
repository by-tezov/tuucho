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
        onResult: OUTPUT.() -> Unit = {},
        onException: ((DomainException) -> Unit)? = null,
    ) {
        try {
            coroutineScopes.useCase.launch {
                when (useCase) {
                    is UseCaseProtocol.Async<INPUT, OUTPUT> -> useCase.invoke(input).also {
                        it.onResult()
                    }

                    is UseCaseProtocol.Sync<INPUT, OUTPUT> -> useCase.invoke(input).also {
                        it.onResult()
                    }
                }
            }
        } catch (e: Throwable) {
            val output: (DomainException) -> Unit = onException ?: { throw it }
            when (e) {
                is DomainException -> output(e)
                else -> output(DomainException.Unknown(e))
            }
        }
    }

    suspend fun <INPUT : Any, OUTPUT : Any> invokeSuspend(
        useCase: UseCaseProtocol<INPUT, OUTPUT>,
        input: INPUT,
    ): OUTPUT {

        try {
            return coroutineScopes.useCase.on {
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

