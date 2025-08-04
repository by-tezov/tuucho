package com.tezov.tuucho.core.domain.business.usecase

import com.tezov.tuucho.core.domain.business.protocol.RefreshCacheMaterialRepositoryProtocol

class RefreshMaterialCacheUseCase(
    private val refreshMaterialCacheRepository: RefreshCacheMaterialRepositoryProtocol,
) {

    suspend fun invoke(url: String) {
        refreshMaterialCacheRepository.process(url)
    }

}