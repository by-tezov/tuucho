package com.tezov.tuucho.core.domain.business.usecase.withNetwork

import com.tezov.tuucho.core.domain.business._system.koin.TuuchoKoinComponent
import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.business.interaction.middleware.NavigationMiddleware
import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.component.SettingComponentShadowerSchema.Key
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareExecutorProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseExecutorProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.NavigationRepositoryProtocol
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.NavigateFinishUseCase
import com.tezov.tuucho.core.domain.test._system.OpenForTest

@OpenForTest
class NavigateBackUseCase(
    private val useCaseExecutor: UseCaseExecutorProtocol,
    private val navigationStackRouteRepository: NavigationRepositoryProtocol.StackRoute,
    private val navigationStackScreenRepository: NavigationRepositoryProtocol.StackScreen,
    private val navigationStackTransitionRepository: NavigationRepositoryProtocol.StackTransition,
    private val middlewareExecutor: MiddlewareExecutorProtocol,
    private val navigationMiddlewares: List<NavigationMiddleware.Back>,
    private val navigateFinish: NavigateFinishUseCase,
    private val navigateShadower: NavigateShadowerUseCase
) : UseCaseProtocol.Async<Unit, Unit>,
    TuuchoKoinComponent {
    override suspend fun invoke(
        input: Unit
    ) {
        middlewareExecutor
            .process(
                middlewares = navigationMiddlewares + terminalMiddleware(),
                context = NavigationMiddleware.Back.Context(
                    currentUrl = navigationStackRouteRepository.currentRoute()?.value
                        ?: throw DomainException.Default("Shouldn't be possible"),
                    nextUrl = navigationStackRouteRepository.priorRoute()?.value,
                )
            )
    }

    private fun terminalMiddleware() = NavigationMiddleware.Back { context, _ ->
        val restoredRoute = navigationStackRouteRepository.backward(
            route = NavigationRoute.Back
        )
        restoredRoute?.let { runShadower(it) }
        navigationStackTransitionRepository.backward()
        navigationStackScreenRepository.sync()
        if (context.nextUrl == null) {
            useCaseExecutor.await(
                useCase = navigateFinish,
                input = Unit
            )
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
