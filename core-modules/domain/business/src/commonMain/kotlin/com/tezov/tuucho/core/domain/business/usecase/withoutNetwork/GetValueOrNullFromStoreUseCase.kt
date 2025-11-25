package com.tezov.tuucho.core.domain.business.usecase.withoutNetwork

import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.KeyValueStoreRepositoryProtocol
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.GetValueOrNullFromStoreUseCase.Input
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.GetValueOrNullFromStoreUseCase.Output

class GetValueOrNullFromStoreUseCase(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val keyValueRepository: KeyValueStoreRepositoryProtocol,
) : UseCaseProtocol.Async<Input, Output> {
    data class Input(
        val key: KeyValueStoreRepositoryProtocol.Key,
    )

    data class Output(
        val value: KeyValueStoreRepositoryProtocol.Value?,
    )

    override suspend fun invoke(
        input: Input
    ) = with(input) {
        coroutineScopes.io.await {
            Output(
                value = keyValueRepository.getOrNull(key)
            )
        }
    }
}
