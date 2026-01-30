package com.tezov.tuucho.core.domain.business.usecase.withoutNetwork

import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.KeyValueStoreRepositoryProtocol
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.RemoveKeyValueFromStoreUseCase.Input
import com.tezov.tuucho.core.domain.test._system.OpenForTest

@OpenForTest
class RemoveKeyValueFromStoreUseCase(
    private val keyValueRepository: KeyValueStoreRepositoryProtocol,
) : UseCaseProtocol.Async<Input, Unit> {
    data class Input(
        val key: KeyValueStoreRepositoryProtocol.Key
    )

    override suspend fun invoke(
        input: Input
    ) = with(input) {
        keyValueRepository.save(key, null)
    }
}
