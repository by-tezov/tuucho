package com.tezov.tuucho.core.domain.business.protocol

import com.tezov.tuucho.core.domain.business.exception.DomainException

interface UseCaseExecutorProtocol {
    fun <INPUT : Any, OUTPUT : Any> async(
        useCase: UseCaseProtocol<INPUT, OUTPUT>,
        input: INPUT,
        onException: ((DomainException) -> Unit)? = null,
        onResult: OUTPUT?.() -> Unit = {},
    )

    suspend fun <INPUT : Any, OUTPUT : Any> await(
        useCase: UseCaseProtocol<INPUT, OUTPUT>,
        input: INPUT,
    ): OUTPUT?
}
