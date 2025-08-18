package com.tezov.tuucho.core.domain.business.usecase

import com.tezov.tuucho.core.domain.business.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.NavigationRepositoryProtocol

class NavigateBackUseCase(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val navigationStackRouteRepository: NavigationRepositoryProtocol.StackRoute,
    private val navigationStackScreenRepository: NavigationRepositoryProtocol.StackScreen,
    private val navigationStackTransitionRepository: NavigationRepositoryProtocol.StackTransition,
) : UseCaseProtocol.Sync<Unit, Unit> {

    override fun invoke(input: Unit) {
        coroutineScopes.navigation.async {
            if(navigationStackTransitionRepository.isBusy()){
                //throw DomainException.Default("Navigation is not ready to accept new request")
                return@async
            }
            navigationStackRouteRepository.backward(
                route = NavigationRoute.Back
            )
            navigationStackTransitionRepository.backward(
                routes = navigationStackRouteRepository.routes(),
            )
            navigationStackScreenRepository.backward(
                routes = navigationStackRouteRepository.routes(),
            )
        }
    }

}