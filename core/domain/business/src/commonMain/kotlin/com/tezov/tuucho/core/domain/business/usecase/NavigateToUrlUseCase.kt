package com.tezov.tuucho.core.domain.business.usecase

import com.tezov.tuucho.core.domain.business.model.schema._system.withScope
import com.tezov.tuucho.core.domain.business.model.schema.material.SettingSchema
import com.tezov.tuucho.core.domain.business.navigation.NavigationDestination
import com.tezov.tuucho.core.domain.business.navigation.NavigationOption
import com.tezov.tuucho.core.domain.business.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.MaterialRepositoryProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.NavigationRepositoryProtocol
import com.tezov.tuucho.core.domain.business.usecase.NavigateToUrlUseCase.Input

class NavigateToUrlUseCase(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val retrieveMaterialRepository: MaterialRepositoryProtocol.Retrieve,
    private val navigationDestinationStackRepository: NavigationRepositoryProtocol.Destination,
    private val navigationScreenStackRepository: NavigationRepositoryProtocol.StackScreen,
    private val shadowerMaterialRepository: MaterialRepositoryProtocol.Shadower,
) : UseCaseProtocol.Async<Input, Unit> {

    data class Input(
        val url: String,
    )

    override suspend fun invoke(input: Input) = with(input) {
        coroutineScopes.onEvent {
            val component = retrieveMaterialRepository.process(url)
            val destination = NavigationDestination(
                route = NavigationRoute.Url(url),
                option = NavigationOption.from(component.withScope(SettingSchema.Root::Scope))
            )
            val events = navigationDestinationStackRepository.swallow(destination)
            navigationScreenStackRepository.swallow(events, component)
            shadowerMaterialRepository.process(url, component)
        }
    }

}