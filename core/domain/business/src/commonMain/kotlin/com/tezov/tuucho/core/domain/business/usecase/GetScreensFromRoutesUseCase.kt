package com.tezov.tuucho.core.domain.business.usecase


import com.tezov.tuucho.core.domain.business.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.NavigationRepositoryProtocol
import com.tezov.tuucho.core.domain.business.protocol.screen.ScreenProtocol
import com.tezov.tuucho.core.domain.business.usecase.GetScreensFromRoutesUseCase.Input
import com.tezov.tuucho.core.domain.business.usecase.GetScreensFromRoutesUseCase.Output

class GetScreensFromRoutesUseCase(
    private val navigationStackScreenRepository: NavigationRepositoryProtocol.StackScreen,
) : UseCaseProtocol.Async<Input, Output> {

    data class Input(
        val routes: List<NavigationRoute>,

        )

    data class Output(
        val screens: List<ScreenProtocol>,

        )

    override suspend fun invoke(input: Input) = with(input) {
        Output(
            screens = navigationStackScreenRepository.getScreens(routes)
        )
    }
}