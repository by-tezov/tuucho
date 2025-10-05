package com.tezov.tuucho.core.domain.business.usecase

import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.KeyValueStoreRepositoryProtocol
import com.tezov.tuucho.core.domain.business.usecase.SaveKeyValueToStoreUseCase.Input

class SaveKeyValueToStoreUseCase(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val keyValueRepository: KeyValueStoreRepositoryProtocol,
) : UseCaseProtocol.Async<Input, Unit> {

    data class Input(
        val key: KeyValueStoreRepositoryProtocol.Key,
        val value: KeyValueStoreRepositoryProtocol.Value?,
    )

    override suspend fun invoke(input: Input) = with(input) {
        coroutineScopes.database.await {
            keyValueRepository.save(input.key, input.value)
        }
    }

}