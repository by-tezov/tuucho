package com.tezov.tuucho.core.domain.business.usecase.withoutNetwork

import com.tezov.tuucho.core.domain.business.model.LanguageModelDomain
import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.GetLanguageUseCase.Output

class GetLanguageUseCase : UseCaseProtocol.Sync<Unit, Output> {

    data class Output(
        val language: LanguageModelDomain,
    )

    override fun invoke(input: Unit) = Output(
        language = LanguageModelDomain.Default //TODO retrieve system language or preference application
    )

}