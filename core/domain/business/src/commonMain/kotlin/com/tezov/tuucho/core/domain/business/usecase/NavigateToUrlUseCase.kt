package com.tezov.tuucho.core.domain.business.usecase

import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.business.model.schema._system.onScope
import com.tezov.tuucho.core.domain.business.model.schema._system.withScope
import com.tezov.tuucho.core.domain.business.model.schema.material.setting.SettingNavigationOptionSchema
import com.tezov.tuucho.core.domain.business.model.schema.material.setting.SettingSchema
import com.tezov.tuucho.core.domain.business.navigation.NavigationDestination
import com.tezov.tuucho.core.domain.business.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.navigation.option.NavigationOption
import com.tezov.tuucho.core.domain.business.navigation.option.PageBreadCrumbNavigationOptionSelector
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.NavigationOptionSelectorProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.MaterialRepositoryProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.NavigationRepositoryProtocol
import com.tezov.tuucho.core.domain.business.usecase.NavigateToUrlUseCase.Input
import com.tezov.tuucho.core.domain.business.usecase._system.UseCaseExecutor
import kotlinx.serialization.json.JsonArray

class NavigateToUrlUseCase(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val useCaseExecutor: UseCaseExecutor,
    private val retrieveMaterialRepository: MaterialRepositoryProtocol.Retrieve,
    private val navigationDestinationStackRepository: NavigationRepositoryProtocol.Destination,
    private val navigationScreenStackRepository: NavigationRepositoryProtocol.StackScreen,
    private val shadowerMaterialRepository: MaterialRepositoryProtocol.Shadower,
    private val navigationOptionSelectorFactory: NavigationOptionSelectorFactoryUseCase,
) : UseCaseProtocol.Async<Input, Unit> {

    data class Input(
        val url: String,
    )

    override suspend fun invoke(input: Input) = with(input) {
        coroutineScopes.onEvent {
            val component = retrieveMaterialRepository.process(url)
            val destination = NavigationDestination(
                route = NavigationRoute.Url(url),
                option = component.onScope(SettingSchema.Root::Scope)
                    .navigationOption.navigationOptionResolver()
            )
            val events = navigationDestinationStackRepository.swallow(destination)
            navigationScreenStackRepository.swallow(events, component)
            shadowerMaterialRepository.process(url, component)
        }
    }

    private suspend fun JsonArray?.navigationOptionResolver() = this?.firstOrNull { optionObject ->
        optionObject.withScope(SettingNavigationOptionSchema.Selector::Scope).self?.let { selector ->
            useCaseExecutor.invokeSuspend(
                useCase = navigationOptionSelectorFactory,
                input = NavigationOptionSelectorFactoryUseCase.Input(
                    prototypeObject = selector
                )
            ).selector.accept()
        } ?: true
    }?.let { optionObject ->
        with(optionObject.withScope(SettingNavigationOptionSchema::Scope)) {
            NavigationOption(
                singleTop = singleTop,
                popUpTo = popupTo?.withScope(SettingNavigationOptionSchema.PopUpTo::Scope)?.let {
                    NavigationOption.PopUpTo(
                        route = NavigationRoute.Url(it.url!!),
                        inclusive = it.inclusive!!
                    )
                },
                clearStack = clearStack
            )
        }
    } ?: NavigationOption()

    private fun NavigationOptionSelectorProtocol.accept() = when (this) {
        is PageBreadCrumbNavigationOptionSelector -> accept(
            navigationDestinationStackRepository
                .stack
                .mapNotNull { (it.route as? NavigationRoute.Url)?.value }
        )

        else -> throw DomainException.Default("Unknown navigation option selector $this")
    }

}