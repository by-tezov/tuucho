package com.tezov.tuucho.core.domain.business.usecase

import com.tezov.tuucho.core.domain.business.action.NavigationUrlActionProcessor
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import com.tezov.tuucho.core.domain.business.usecase._system.UseCaseExecutor

class RegisterToNavigationUrlActionEventUseCase(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val useCaseExecutor: UseCaseExecutor,
    private val navigationUrlActionProcessor: NavigationUrlActionProcessor,
    private val navigateForward: NavigateToUrlUseCase,
) : UseCaseProtocol.Sync<Unit, Unit> {

    override fun invoke(input: Unit) {
        coroutineScopes.event.async {
            navigationUrlActionProcessor.events
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