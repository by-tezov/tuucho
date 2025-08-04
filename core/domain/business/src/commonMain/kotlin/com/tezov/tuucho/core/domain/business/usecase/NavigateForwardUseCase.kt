package com.tezov.tuucho.core.domain.business.usecase

import com.tezov.tuucho.core.domain.business.model.schema._system.withScope
import com.tezov.tuucho.core.domain.business.model.schema.material.SettingSchema
import com.tezov.tuucho.core.domain.business.navigation.NavigationDestination
import com.tezov.tuucho.core.domain.business.navigation.NavigationOption
import com.tezov.tuucho.core.domain.business.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.navigation.protocol.NavigationStackRepositoryProtocol
import com.tezov.tuucho.core.domain.business.navigation.protocol.ViewContextStackRepositoryProtocol

class NavigateForwardUseCase(
    private val navigationRepository: NavigationStackRepositoryProtocol,
    private val viewStackRepository: ViewContextStackRepositoryProtocol,
    private val retrieveComponent: RetrieveComponentUseCase,
) {

    suspend fun invoke(url: String) {
        val component = retrieveComponent.invoke(url)
        val destination = NavigationDestination(
            route = NavigationRoute.Url(url),
            option = NavigationOption.from(component.withScope(SettingSchema.Root::Scope))
        )
        val stack = navigationRepository.swallow(destination)
        viewStackRepository.swallow(stack, component)
    }

}