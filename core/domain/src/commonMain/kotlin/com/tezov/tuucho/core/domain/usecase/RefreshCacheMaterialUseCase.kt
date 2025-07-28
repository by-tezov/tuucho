package com.tezov.tuucho.core.domain.usecase

import com.tezov.tuucho.core.domain.protocol.CoroutineContextProviderProtocol
import com.tezov.tuucho.core.domain.protocol.RefreshCacheMaterialRepositoryProtocol
import kotlinx.coroutines.withContext

class RefreshCacheMaterialUseCase(
    private val coroutineDispatchers: CoroutineContextProviderProtocol,
    private val repository: RefreshCacheMaterialRepositoryProtocol
) {

    suspend fun invoke(url: String) {
        withContext(coroutineDispatchers.io) {
            repository.process(url)
        }
    }

}