package com.tezov.tuucho.core.domain.business.usecase

import com.tezov.tuucho.core.domain.business.navigation.NavigationDestination
import com.tezov.tuucho.core.domain.business.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.NavigationRepositoryProtocol

class NavigateBackUseCase(
    private val navigationDestinationStackRepository: NavigationRepositoryProtocol.Destination,
    private val navigationScreenStackRepository: NavigationRepositoryProtocol.StackScreen,
) : UseCaseProtocol.Async<Unit, Unit> {

    override suspend fun invoke(input: Unit) = with(input) {
        val events = navigationDestinationStackRepository.swallow(
            NavigationDestination(
                route = NavigationRoute.Back
            )
        )
        navigationScreenStackRepository.swallow(events)
    }

}