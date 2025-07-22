package com.tezov.tuucho.core.domain.usecase

import com.tezov.tuucho.core.domain.protocol.CoroutineDispatchersProtocol
import com.tezov.tuucho.core.domain.protocol.MaterialRepositoryProtocol
import kotlinx.coroutines.withContext

class RefreshCacheMaterialUseCase(
    private val coroutineDispatchers: CoroutineDispatchersProtocol,
    private val repository: MaterialRepositoryProtocol
) {

    suspend fun invoke(url: String) = withContext(coroutineDispatchers.io) {
        repository.refreshCache(url)
    }

}