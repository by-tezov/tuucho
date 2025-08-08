package com.tezov.tuucho.core.domain.business.usecase

import com.tezov.tuucho.core.domain.business.protocol.SourceIdentifierProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.NavigationRepositoryProtocol
import com.tezov.tuucho.core.domain.business.protocol.screen.ScreenProtocol
import com.tezov.tuucho.core.domain.business.usecase.GetOrNullScreenUseCase.Input
import com.tezov.tuucho.core.domain.business.usecase.GetOrNullScreenUseCase.Output

class GetOrNullScreenUseCase(
    private val navigationScreenStackRepository: NavigationRepositoryProtocol.StackScreen,
) : UseCaseProtocol.Async<Input, Output> {

    data class Input(
        val screenIdentifier: SourceIdentifierProtocol,
    )

    data class Output(
        val screen: ScreenProtocol?,
    )

    override suspend fun invoke(input: Input) = with(input) {
        Output(
            screen = navigationScreenStackRepository.getView(screenIdentifier)
        )
    }
}