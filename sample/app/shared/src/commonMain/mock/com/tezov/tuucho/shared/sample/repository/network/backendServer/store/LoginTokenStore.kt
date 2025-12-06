package com.tezov.tuucho.shared.sample.repository.network.backendServer.store

import com.tezov.tuucho.core.domain.business.protocol.UseCaseExecutorProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.KeyValueStoreRepositoryProtocol.Key.Companion.toKey
import com.tezov.tuucho.core.domain.business.protocol.repository.KeyValueStoreRepositoryProtocol.Value.Companion.toValue
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.GetValueOrNullFromStoreUseCase
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.SaveKeyValueToStoreUseCase

class LoginTokenStore(
    private val useCaseExecutor: UseCaseExecutorProtocol,
    private val saveKeyValueToStore: SaveKeyValueToStoreUseCase,
    private val getValueOrNullFromStore: GetValueOrNullFromStoreUseCase,
) {
    suspend fun isValid(token: String): Boolean {
        if (token.isBlank()) return false
        return useCaseExecutor.await(
            useCase = getValueOrNullFromStore,
            input = GetValueOrNullFromStoreUseCase.Input(
                key = token.toKey()
            )
        )?.value?.value != null
    }

    suspend fun setToken(login: String, token: String) {
        useCaseExecutor.await(
            useCase = saveKeyValueToStore,
            input = SaveKeyValueToStoreUseCase.Input(
                key = token.toKey(),
                value = login.toValue()
            )
        )
    }
}
