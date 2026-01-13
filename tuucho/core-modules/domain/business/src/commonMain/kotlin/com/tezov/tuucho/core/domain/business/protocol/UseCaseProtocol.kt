package com.tezov.tuucho.core.domain.business.protocol

sealed interface UseCaseProtocol<INPUT : Any, OUTPUT : Any> {
    interface Async<INPUT : Any, OUTPUT : Any> : UseCaseProtocol<INPUT, OUTPUT> {
        suspend fun invoke(
            input: INPUT
        ): OUTPUT?
    }

    interface Sync<INPUT : Any, OUTPUT : Any> : UseCaseProtocol<INPUT, OUTPUT> {
        fun invoke(
            input: INPUT
        ): OUTPUT?
    }
}
