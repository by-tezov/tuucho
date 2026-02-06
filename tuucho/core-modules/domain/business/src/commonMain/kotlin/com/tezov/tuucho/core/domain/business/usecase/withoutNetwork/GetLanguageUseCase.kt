package com.tezov.tuucho.core.domain.business.usecase.withoutNetwork

import com.tezov.tuucho.core.domain.business.model.LanguageModelDomain
import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.SystemPlatformRepositoryProtocol
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.GetLanguageUseCase.Output
import com.tezov.tuucho.core.domain.test._system.OpenForTest

@OpenForTest
class GetLanguageUseCase(
    private val platformRepository: SystemPlatformRepositoryProtocol
) : UseCaseProtocol.Async<Unit, Output> {
    data class Output(
        val language: LanguageModelDomain?,
    )

    override suspend fun invoke(
        input: Unit
    ) = Output(
        language = platformRepository.getCurrentLanguage()
    )
}
