package com.tezov.tuucho.core.domain.business.usecase.withoutNetwork

import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.KeyValueStoreRepositoryProtocol
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.RemoveKeyValueFromStoreUseCase.Input

class RemoveKeyValueFromStoreUseCase(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val keyValueRepository: KeyValueStoreRepositoryProtocol,
) : UseCaseProtocol.Async<Input, Unit> {
    data class Input(
        val key: KeyValueStoreRepositoryProtocol.Key
    )

    override suspend fun invoke(
        input: Input
    ) = with(input) {
        coroutineScopes.io.await {
            keyValueRepository.save(key, null)
        }
    }
}
