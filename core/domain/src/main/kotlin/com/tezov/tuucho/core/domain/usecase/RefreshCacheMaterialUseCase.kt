package com.tezov.tuucho.core.domain.usecase

import com.tezov.tuucho.core.domain.protocol.MaterialRepositoryProtocol

class RefreshCacheMaterialUseCase(private val repository: MaterialRepositoryProtocol) {

    suspend fun invoke(url: String) = repository.refreshCache(url)

}