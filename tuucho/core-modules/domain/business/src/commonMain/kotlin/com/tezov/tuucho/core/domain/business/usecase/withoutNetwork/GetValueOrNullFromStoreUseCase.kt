package com.tezov.tuucho.core.domain.business.usecase.withoutNetwork

import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.KeyValueStoreRepositoryProtocol
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.GetValueOrNullFromStoreUseCase.Input
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.GetValueOrNullFromStoreUseCase.Output
import com.tezov.tuucho.core.domain.test._system.OpenForTest

@OpenForTest
class GetValueOrNullFromStoreUseCase(
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
        Output(
            value = keyValueRepository.getOrNull(key)
        )
    }
}
