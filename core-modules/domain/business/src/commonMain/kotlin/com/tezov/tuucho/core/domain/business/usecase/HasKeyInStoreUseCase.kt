package com.tezov.tuucho.core.domain.business.usecase

import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.KeyValueStoreRepositoryProtocol
import com.tezov.tuucho.core.domain.business.usecase.HasKeyInStoreUseCase.Input
import com.tezov.tuucho.core.domain.business.usecase.HasKeyInStoreUseCase.Output

class HasKeyInStoreUseCase(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val keyValueRepository: KeyValueStoreRepositoryProtocol,
) : UseCaseProtocol.Async<Input, Output> {

    data class Input(
        val key: KeyValueStoreRepositoryProtocol.Key
    )

    data class Output(
        val result: Boolean,
    )

    override suspend fun invoke(input: Input) = with(input) {
        coroutineScopes.database.await {
            Output(
                result = keyValueRepository.hasKey(input.key)
            )
        }
    }

}