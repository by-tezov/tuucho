package com.tezov.tuucho.core.domain.usecase

import com.tezov.tuucho.core.domain.protocol.RefreshCacheMaterialRepositoryProtocol

class RefreshMaterialCacheUseCase(
    private val refreshMaterialCacheRepository: RefreshCacheMaterialRepositoryProtocol
) {

    suspend fun invoke(url: String) {
        refreshMaterialCacheRepository.process(url)
    }

}