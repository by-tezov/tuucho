package com.tezov.tuucho.core.domain.business.usecase

import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.NavigationRepositoryProtocol
import com.tezov.tuucho.core.domain.business.protocol.screen.ScreenProtocol
import com.tezov.tuucho.core.domain.business.usecase.GetScreenOrNullUseCase.Input
import com.tezov.tuucho.core.domain.business.usecase.GetScreenOrNullUseCase.Output

class GetScreenOrNullUseCase(
    private val navigationStackScreenRepository: NavigationRepositoryProtocol.StackScreen,
) : UseCaseProtocol.Async<Input, Output> {

    data class Input(
        val route: NavigationRoute,
    )

    data class Output(
        val screen: ScreenProtocol?,
    )

    override suspend fun invoke(input: Input) = with(input) {
        Output(
            screen = navigationStackScreenRepository.getScreenOrNull(route)
        )
    }
}