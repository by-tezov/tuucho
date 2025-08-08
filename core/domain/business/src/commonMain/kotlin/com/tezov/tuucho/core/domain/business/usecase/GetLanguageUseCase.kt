package com.tezov.tuucho.core.domain.business.usecase

import com.tezov.tuucho.core.domain.business.config.Language
import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import com.tezov.tuucho.core.domain.business.usecase.GetLanguageUseCase.Output

class GetLanguageUseCase : UseCaseProtocol.Sync<Unit, Output> {

    data class Output(
        val language: Language,
    )

    override fun invoke(input: Unit) = Output(
        language = Language.Default //TODO retrieve system language or preference application
    )

}