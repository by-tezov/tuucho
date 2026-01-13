package com.tezov.tuucho.core.domain.business.usecase.withoutNetwork

import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.KeyValueStoreRepositoryProtocol
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.SaveKeyValueToStoreUseCase.Input
import com.tezov.tuucho.core.domain.test._system.OpenForTest

@OpenForTest
class SaveKeyValueToStoreUseCase(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val keyValueRepository: KeyValueStoreRepositoryProtocol,
) : UseCaseProtocol.Async<Input, Unit> {
    data class Input(
        val key: KeyValueStoreRepositoryProtocol.Key,
        val value: KeyValueStoreRepositoryProtocol.Value?,
    )

    override suspend fun invoke(
        input: Input
    ) = with(input) {
        coroutineScopes.io.await {
            keyValueRepository.save(key, value)
        }
    }
}
