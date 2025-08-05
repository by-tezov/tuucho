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
        onException: (DomainException) -> Unit = {},
    ) {
        runCatching {
            coroutineScopes.launchOnEvent {
                when (useCase) {
                    is UseCaseProtocol.Async<INPUT, OUTPUT> -> useCase.invoke(input).also {
                        it.onResult()
                    }

                    is UseCaseProtocol.Sync<INPUT, OUTPUT> -> useCase.invoke(input).also {
                        it.onResult()
                    }
                }
            }
        }.onFailure {
            when (it) {
                is DomainException -> onException(it)
                else -> onException(DomainException.Unknown(it))
            }
        }
    }

    suspend fun <INPUT : Any, OUTPUT : Any> invokeSuspend(
        useCase: UseCaseProtocol<INPUT, OUTPUT>,
        input: INPUT,
    ): OUTPUT = runCatching {
        coroutineScopes.onEvent {
            when (useCase) {
                is UseCaseProtocol.Async<INPUT, OUTPUT> -> useCase.invoke(input)
                is UseCaseProtocol.Sync<INPUT, OUTPUT> -> useCase.invoke(input)
            }
        }
    }.onFailure {
        when (it) {
            is DomainException -> it
            else -> DomainException.Unknown(it)
        }
    }.getOrThrow()

}

