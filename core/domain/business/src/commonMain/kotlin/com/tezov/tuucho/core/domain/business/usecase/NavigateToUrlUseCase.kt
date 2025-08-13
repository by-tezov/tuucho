package com.tezov.tuucho.core.domain.business.usecase

import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.business.jsonSchema._system.onScope
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.SettingNavigationOptionSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.SettingSchema
import com.tezov.tuucho.core.domain.business.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.navigation.NavigationRouteIdGenerator
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
    private val navigationRouteIdGenerator: NavigationRouteIdGenerator,
    private val navigationStackRouteRepository: NavigationRepositoryProtocol.StackRoute,
    private val navigationStackScreenRepository: NavigationRepositoryProtocol.StackScreen,
    private val navigationStackAnimatorRepository: NavigationRepositoryProtocol.StackAnimator,
    private val shadowerMaterialRepository: MaterialRepositoryProtocol.Shadower,
    private val navigationOptionSelectorFactory: NavigationOptionSelectorFactoryUseCase,
) : UseCaseProtocol.Sync<Input, Unit> {

    data class Input(
        val url: String,
    )

    override fun invoke(input: Input) {
        coroutineScopes.navigation.async {
            with(input) {
                val component = retrieveMaterialRepository.process(url)
                val newRoute = navigationStackRouteRepository.swallow(
                    route = NavigationRoute.Url(navigationRouteIdGenerator.generate(), url),
                    option = component.onScope(SettingSchema.Root::Scope)
                        .navigationOption.navigationOptionResolver()
                )
                newRoute?.let {
                    navigationStackScreenRepository.swallow(
                        route = it,
                        componentObject = component
                    )
                }
                shadowerMaterialRepository.process(url, component)
                navigationStackAnimatorRepository.swallow(
                    routes = navigationStackRouteRepository.routes(),
                    animationObject = component,
                )
                navigationStackScreenRepository.spit(
                    routes = navigationStackRouteRepository.routes()
                )

                println("********************")
                println(navigationStackRouteRepository.routes())
                println(navigationStackScreenRepository.routes())
                println(navigationStackAnimatorRepository.routes())
            }
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
                single = single ?: false,
                reuse = reuse,
                popUpTo = popupTo?.withScope(SettingNavigationOptionSchema.PopUpTo::Scope)?.let {
                    NavigationOption.PopUpTo(
                        route = NavigationRoute.Url("", it.url!!),
                        inclusive = it.inclusive ?: false,
                        greedy = it.greedy ?: true
                    )
                },
                clearStack = clearStack ?: false
            )
        }
    } ?: NavigationOption(
        single = false,
        reuse = SettingNavigationOptionSchema.Value.Reuse.last,
        popUpTo = null,
        clearStack = false
    )

    private suspend fun NavigationOptionSelectorProtocol.accept() = when (this) {
        is PageBreadCrumbNavigationOptionSelector -> accept(
            navigationStackRouteRepository
                .routes()
                .mapNotNull { (it as? NavigationRoute.Url)?.value }
        )

        else -> throw DomainException.Default("Unknown navigation option selector $this")
    }

}