package com.tezov.tuucho.core.domain.business.usecase.withNetwork

import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.MaterialRepositoryProtocol

class RefreshMaterialCacheUseCase(
    private val refreshMaterialCacheRepository: MaterialRepositoryProtocol.RefreshCache,
) : UseCaseProtocol.Async<RefreshMaterialCacheUseCase.Input, Unit> {
    data class Input(
        val url: String,
    )

    override suspend fun invoke(
        input: Input
    ) {
        with(input) {
            refreshMaterialCacheRepository.process(url)
        }
    }
}
