package com.tezov.tuucho.core.domain.business.usecase.withNetwork

import com.tezov.tuucho.core.domain.business._system.koin.TuuchoKoinComponent
import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRouteIdGenerator
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.component.SettingComponentShadowerSchema.Key
import com.tezov.tuucho.core.domain.business.middleware.NavigationMiddleware
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareExecutorProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseExecutorProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.NavigationRepositoryProtocol
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.NavigateToUrlUseCase.Input
import com.tezov.tuucho.core.domain.test._system.OpenForTest

@OpenForTest
class NavigateToUrlUseCase(
    private val useCaseExecutor: UseCaseExecutorProtocol,
    private val navigationRouteIdGenerator: NavigationRouteIdGenerator,
    private val navigationStackRouteRepository: NavigationRepositoryProtocol.StackRoute,
    private val navigationStackScreenRepository: NavigationRepositoryProtocol.StackScreen,
    private val navigationStackTransitionRepository: NavigationRepositoryProtocol.StackTransition,
    private val middlewareExecutor: MiddlewareExecutorProtocol,
    private val navigationMiddlewares: List<NavigationMiddleware.ToUrl>,
    private val navigateShadower: NavigateShadowerUseCase
) : UseCaseProtocol.Async<Input, Unit>,
    TuuchoKoinComponent {
    data class Input(
        val url: String
    )

    override suspend fun invoke(
        input: Input
    ) {
        middlewareExecutor
            .process(
                middlewares = navigationMiddlewares + terminalMiddleware(),
                context = NavigationMiddleware.ToUrl.Context(
                    currentUrl = navigationStackRouteRepository.currentRoute()?.value,
                    input = input,
                )
            )
    }

    private fun terminalMiddleware() = NavigationMiddleware.ToUrl { context, _ ->
        with(context.input) {
            val inputRoute = NavigationRoute.Url(
                id = navigationRouteIdGenerator.generate(),
                value = url
            )
            val outputRoute = navigationStackRouteRepository
                .forward(route = inputRoute)
            if (outputRoute.id == inputRoute.id) {
                navigationStackScreenRepository
                    .forward(route = outputRoute)
                runShadower(route = outputRoute)
            }
            navigationStackTransitionRepository
                .forward(route = outputRoute)
        }
    }

    private suspend fun runShadower(
        route: NavigationRoute.Url
    ) {
        useCaseExecutor.await(
            useCase = navigateShadower,
            input = NavigateShadowerUseCase.Input(
                route = route,
                direction = Key.navigateForward
            )
        )
    }
}
