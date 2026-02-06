package com.tezov.tuucho.core.domain.business.usecase.withoutNetwork

import com.tezov.tuucho.core.domain.business.model.LanguageModelDomain
import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.SystemPlatformRepositoryProtocol
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.SetLanguageUseCase.Input
import com.tezov.tuucho.core.domain.test._system.OpenForTest

@OpenForTest
class SetLanguageUseCase(
    private val platformRepository: SystemPlatformRepositoryProtocol
) : UseCaseProtocol.Async<Input, Unit> {
    data class Input(
        val language: LanguageModelDomain,
    )

    override suspend fun invoke(
        input: Input
    ) {
        platformRepository.setCurrentLanguage(input.language)
    }
}
