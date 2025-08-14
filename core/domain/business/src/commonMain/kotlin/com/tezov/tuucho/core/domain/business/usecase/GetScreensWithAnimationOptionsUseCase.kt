package com.tezov.tuucho.core.domain.business.usecase

import com.tezov.tuucho.core.domain.business.navigation.transitionOption.AnimationScreen
import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.NavigationRepositoryProtocol
import com.tezov.tuucho.core.domain.business.protocol.screen.ScreenProtocol
import com.tezov.tuucho.core.domain.business.usecase.GetScreensWithAnimationOptionsUseCase.Output

class GetScreensWithAnimationOptionsUseCase(
    private val navigationStackScreenRepository: NavigationRepositoryProtocol.StackScreen,
    private val navigationStackTransitionRepository: NavigationRepositoryProtocol.StackTransition,
) : UseCaseProtocol.Async<Unit, List<Output>> {

    data class Output(
        val screen: ScreenProtocol,
        val animationScreen: AnimationScreen,
    )

    override suspend fun invoke(input: Unit): List<Output> {
        return navigationStackTransitionRepository
            .getRoutesWithAnimationOptions()
            .mapNotNull { routeWithAnimationOption ->
                navigationStackScreenRepository
                    .getScreenOrNull(routeWithAnimationOption.route)
                    ?.let { screen ->
                        Output(
                            screen = screen,
                            animationScreen = routeWithAnimationOption.animationScreen
                        )
                    }
            }

    }
}