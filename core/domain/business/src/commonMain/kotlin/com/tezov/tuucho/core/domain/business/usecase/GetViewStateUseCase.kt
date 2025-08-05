package com.tezov.tuucho.core.domain.business.usecase

import com.tezov.tuucho.core.domain.business.navigation.protocol.ViewContextStackRepositoryProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import com.tezov.tuucho.core.domain.business.protocol.state.StateViewProtocol

class GetViewStateUseCase(
    private val viewStackRepository: ViewContextStackRepositoryProtocol,
) : UseCaseProtocol.Async<GetViewStateUseCase.Input, GetViewStateUseCase.Output> {

    data class Input(
        val url: String,
    )

    data class Output(
        val state: StateViewProtocol,
    )

    override suspend fun invoke(input: Input) = with(input) {
        Output(
            viewStackRepository.getViewState(url)
        )
    }
}