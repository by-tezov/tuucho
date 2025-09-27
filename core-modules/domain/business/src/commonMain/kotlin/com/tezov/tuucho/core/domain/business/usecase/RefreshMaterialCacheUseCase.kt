package com.tezov.tuucho.core.domain.business.usecase

import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.MaterialRepositoryProtocol
import com.tezov.tuucho.core.domain.business.usecase.RefreshMaterialCacheUseCase.Input

class RefreshMaterialCacheUseCase(
    private val refreshMaterialCacheRepository: MaterialRepositoryProtocol.RefreshCache,
) : UseCaseProtocol.Async<Input, Unit> {

    data class Input(
        val url: String,
    )

    override suspend fun invoke(input: Input) {
        with(input) {
            refreshMaterialCacheRepository.process(url)
        }
    }

}