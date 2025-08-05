package com.tezov.tuucho.core.domain.business.usecase

import com.tezov.tuucho.core.domain.business.navigation.protocol.ViewContextStackRepositoryProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import com.tezov.tuucho.core.domain.business.protocol.ViewProtocol
import com.tezov.tuucho.core.domain.business.usecase.GetLastViewUseCase.Output

class GetLastViewUseCase(
    private val viewStackRepository: ViewContextStackRepositoryProtocol,
) : UseCaseProtocol.Async<Unit, Output> {

    data class Output(
        val view: ViewProtocol?,
    )

    override suspend fun invoke(input: Unit) = Output(
        view = viewStackRepository.currentViewContext?.view?.value
    )

}