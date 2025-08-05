package com.tezov.tuucho.core.domain.business.usecase

import com.tezov.tuucho.core.domain.business.model.schema._system.withScope
import com.tezov.tuucho.core.domain.business.model.schema.material.SettingSchema
import com.tezov.tuucho.core.domain.business.navigation.NavigationDestination
import com.tezov.tuucho.core.domain.business.navigation.NavigationOption
import com.tezov.tuucho.core.domain.business.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.navigation.protocol.NavigationStackRepositoryProtocol
import com.tezov.tuucho.core.domain.business.navigation.protocol.ViewContextStackRepositoryProtocol
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.RetrieveMaterialRepositoryProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import com.tezov.tuucho.core.domain.business.usecase.NavigateToUrlUseCase.Input

class NavigateToUrlUseCase(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val retrieveMaterialRepository: RetrieveMaterialRepositoryProtocol,
    private val navigationStackRepository: NavigationStackRepositoryProtocol,
    private val viewContextStackRepository: ViewContextStackRepositoryProtocol,
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
            val events = navigationStackRepository.swallow(destination)
            viewContextStackRepository.swallow(events, component)
        }
    }

}