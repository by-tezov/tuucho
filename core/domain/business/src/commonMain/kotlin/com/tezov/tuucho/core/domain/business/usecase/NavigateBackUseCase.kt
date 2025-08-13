package com.tezov.tuucho.core.domain.business.usecase

import com.tezov.tuucho.core.domain.business.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.NavigationRepositoryProtocol

class NavigateBackUseCase(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val navigationStackRouteRepository: NavigationRepositoryProtocol.StackRoute,
    private val navigationStackScreenRepository: NavigationRepositoryProtocol.StackScreen,
    private val navigationStackAnimatorRepository: NavigationRepositoryProtocol.StackAnimator,
) : UseCaseProtocol.Sync<Unit, Unit> {

    override fun invoke(input: Unit) {
        coroutineScopes.navigation.async {
            navigationStackRouteRepository.spit(
                route = NavigationRoute.Back
            )
            navigationStackAnimatorRepository.spit(
                routes = navigationStackRouteRepository.routes(),
            )
            navigationStackScreenRepository.spit(
                routes = navigationStackRouteRepository.routes(),
            )

            println("********************")
            println(navigationStackRouteRepository.routes())
            println(navigationStackScreenRepository.routes())
            println(navigationStackAnimatorRepository.routes())
        }
    }

}