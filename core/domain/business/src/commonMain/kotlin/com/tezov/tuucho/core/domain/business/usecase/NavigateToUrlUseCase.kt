package com.tezov.tuucho.core.domain.business.usecase

import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.business.jsonSchema._system.onScope
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.SettingNavigationSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.SettingNavigationSchema.Selector
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.SettingSchema
import com.tezov.tuucho.core.domain.business.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.navigation.NavigationRouteIdGenerator
import com.tezov.tuucho.core.domain.business.navigation.selector.PageBreadCrumbNavigationOptionSelector
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.NavigationOptionSelectorProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.MaterialRepositoryProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.NavigationRepositoryProtocol
import com.tezov.tuucho.core.domain.business.usecase.NavigateToUrlUseCase.Input
import com.tezov.tuucho.core.domain.business.usecase._system.UseCaseExecutor
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

class NavigateToUrlUseCase(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val useCaseExecutor: UseCaseExecutor,
    private val retrieveMaterialRepository: MaterialRepositoryProtocol.Retrieve,
    private val navigationRouteIdGenerator: NavigationRouteIdGenerator,
    private val navigationOptionSelectorFactory: NavigationOptionSelectorFactoryUseCase,
    private val navigationStackRouteRepository: NavigationRepositoryProtocol.StackRoute,
    private val navigationStackScreenRepository: NavigationRepositoryProtocol.StackScreen,
    private val navigationStackTransitionRepository: NavigationRepositoryProtocol.StackTransition,
    private val shadowerMaterialRepository: MaterialRepositoryProtocol.Shadower,
) : UseCaseProtocol.Sync<Input, Unit> {

    data class Input(
        val url: String,
    )

    override fun invoke(input: Input) {
        coroutineScopes.navigation.async {
            if (navigationStackTransitionRepository.isBusy()) {
                //throw DomainException.Default("Navigation is not ready to accept new request")
                return@async
            }
            with(input) {
                val componentObject = retrieveMaterialRepository.process(url)
                val navigationSettingObject = componentObject
                    .onScope(SettingSchema.Root::Scope)
                    .navigation?.navigationResolver()

                val newRoute = navigationStackRouteRepository.push(
                    route = NavigationRoute.Url(navigationRouteIdGenerator.generate(), url),
                    navigationOptionObject = navigationSettingObject
                        ?.withScope(SettingNavigationSchema.Option::Scope)?.self
                )
                newRoute?.let {
                    navigationStackScreenRepository.push(
                        route = it,
                        componentObject = componentObject
                    )
                    shadowerMaterialRepository.process(url, componentObject)
                }
                navigationStackTransitionRepository.swallow(
                    routes = navigationStackRouteRepository.routes(),
                    navigationTransitionScreenObject = navigationSettingObject
                        ?.withScope(SettingNavigationSchema.Transition::Scope)?.self,
                )
                navigationStackScreenRepository.intersect(
                    routes = navigationStackRouteRepository.routes()
                )
            }
        }
    }

    private suspend fun JsonArray?.navigationResolver() = this
        ?.firstOrNull { it.accept() }
        ?.let { it as? JsonObject }

    private suspend fun JsonElement.accept(): Boolean {
        val selector = withScope(Selector::Scope).self ?: return true
        return useCaseExecutor.invokeSuspend(
            useCase = navigationOptionSelectorFactory,
            input = NavigationOptionSelectorFactoryUseCase.Input(
                prototypeObject = selector
            )
        ).selector.accept()
    }

    private suspend fun NavigationOptionSelectorProtocol.accept() = when (this) {
        is PageBreadCrumbNavigationOptionSelector -> {
            val route = navigationStackRouteRepository.routes()
            accept(route.mapNotNull { (it as? NavigationRoute.Url)?.value })
        }

        else -> throw DomainException.Default("Unknown navigation option selector $this")
    }

}