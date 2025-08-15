package com.tezov.tuucho.core.domain.business.usecase

import com.tezov.tuucho.core.domain.business.navigation.transition.TransitionScreen
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
        val transitionScreen: TransitionScreen,
    )

    override suspend fun invoke(input: Unit): List<Output> {
        TODO()
//        return navigationStackTransitionRepository
//            .getRoutesWithAnimationOptions()
//            .mapNotNull { routeWithAnimationOption ->
//                navigationStackScreenRepository
//                    .getScreenOrNull(routeWithAnimationOption.route)
//                    ?.let { screen ->
//                        Output(
//                            screen = screen,
//                            transitionScreen = routeWithAnimationOption.transitionScreen
//                        )
//                    }
//            }

    }
}