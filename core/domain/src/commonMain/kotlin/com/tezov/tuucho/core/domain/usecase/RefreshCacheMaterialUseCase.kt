package com.tezov.tuucho.core.domain.usecase

import com.tezov.tuucho.core.domain.protocol.RefreshCacheMaterialRepositoryProtocol

class RefreshCacheMaterialUseCase(
    private val refreshCacheMaterialRepository: RefreshCacheMaterialRepositoryProtocol
) {

    suspend fun invoke(url: String) {
        refreshCacheMaterialRepository.process(url)
    }

}