package com.tezov.tuucho.core.domain.business.usecase

import com.tezov.tuucho.core.domain.business.actionHandler.NavigationUrlActionHandler
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import com.tezov.tuucho.core.domain.business.usecase._system.UseCaseExecutor

class RegisterToNavigationUrlActionEventUseCase(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val useCaseExecutor: UseCaseExecutor,
    private val navigationUrlActionHandler: NavigationUrlActionHandler,
    private val navigateForward: NavigateToUrlUseCase,
) : UseCaseProtocol.Async<Unit, Unit> {

    override suspend fun invoke(input: Unit) {
        coroutineScopes.onEvent {
            navigationUrlActionHandler.events
                .forever { url ->
                    useCaseExecutor.invokeSuspend(
                        useCase = navigateForward,
                        input = NavigateToUrlUseCase.Input(
                            url = url
                        )
                    )
                }
        }
    }
}