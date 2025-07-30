package com.tezov.tuucho.core.domain.usecase

import com.tezov.tuucho.core.domain.protocol.RefreshCacheMaterialRepositoryProtocol

class RefreshCacheMaterialUseCase(
    private val repository: RefreshCacheMaterialRepositoryProtocol
) {

    suspend fun invoke(url: String) {
        repository.process(url)
    }

}