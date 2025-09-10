package com.tezov.tuucho.core.domain.business.usecase

import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.business.jsonSchema._system.onScope
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.Shadower
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.component.ComponentSettingSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.component.navigationSchema.ComponentSettingNavigationSchema
import com.tezov.tuucho.core.domain.business.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.navigation.NavigationRouteIdGenerator
import com.tezov.tuucho.core.domain.business.navigation.selector.PageBreadCrumbNavigationDefinitionSelectorMatcher
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.NavigationDefinitionSelectorMatcherProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.MaterialRepositoryProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.NavigationRepositoryProtocol
import com.tezov.tuucho.core.domain.business.usecase.NavigateToUrlUseCase.Input
import com.tezov.tuucho.core.domain.business.usecase._system.UseCaseExecutor
import com.tezov.tuucho.core.domain.tool.extension.ExtensionBoolean.isTrue
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject

class NavigateToUrlUseCase(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val useCaseExecutor: UseCaseExecutor,
    private val retrieveMaterialRepository: MaterialRepositoryProtocol.Retrieve,
    private val navigationRouteIdGenerator: NavigationRouteIdGenerator,
    private val navigationOptionSelectorFactory: NavigationDefinitionSelectorMatcherFactoryUseCase,
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
                val componentSettingScope = componentObject.onScope(ComponentSettingSchema.Root::Scope)
                val navigationSettingObject = componentSettingScope.navigation
                val navigationDefinitionObject = navigationSettingObject
                    ?.withScope(ComponentSettingNavigationSchema::Scope)
                    ?.definition?.navigationResolver()
                val newRoute = navigationStackRouteRepository.forward(
                    route = NavigationRoute.Url(navigationRouteIdGenerator.generate(), url),
                    navigationOptionObject = navigationDefinitionObject
                        ?.withScope(ComponentSettingNavigationSchema.Definition::Scope)?.option
                )
                newRoute?.let {
                    val newScreen = navigationStackScreenRepository.forward(
                        route = newRoute,
                        componentObject = componentObject
                    )
                    val job = coroutineScopes.navigation.async {
                        shadowerMaterialRepository.process(url, componentObject)
                            .filter { it.type == Shadower.Type.contextual }
                            .forEach {
                                coroutineScopes.renderer.await {
                                    newScreen.update(it.jsonObject)
                                }
                        }
                    }
                    if(componentSettingScope.waitContextualShadower.isTrue){
                        job.await()
                    }
                }
                navigationStackTransitionRepository.forward(
                    routes = navigationStackRouteRepository.routes(),
                    navigationExtraObject = navigationSettingObject
                        ?.withScope(ComponentSettingNavigationSchema::Scope)
                        ?.extra,
                    navigationTransitionObject = navigationDefinitionObject
                        ?.withScope(ComponentSettingNavigationSchema.Definition::Scope)?.transition,
                )
                navigationStackScreenRepository.backward(
                    routes = navigationStackRouteRepository.routes()
                )
            }
        }
    }

    private suspend fun JsonArray?.navigationResolver() = this
        ?.firstOrNull { it.jsonObject.accept() }
        ?.let { it as? JsonObject }

    private suspend fun JsonObject.accept(): Boolean {
        val selector = withScope(ComponentSettingNavigationSchema.Definition::Scope).selector ?: return true
        return useCaseExecutor.invokeSuspend(
            useCase = navigationOptionSelectorFactory,
            input = NavigationDefinitionSelectorMatcherFactoryUseCase.Input(
                prototypeObject = selector
            )
        ).selector.accept()
    }

    private suspend fun NavigationDefinitionSelectorMatcherProtocol.accept() = when (this) {
        is PageBreadCrumbNavigationDefinitionSelectorMatcher -> {
            val route = navigationStackRouteRepository.routes()
            accept(route.mapNotNull { (it as? NavigationRoute.Url)?.value })
        }

        else -> throw DomainException.Default("Unknown navigation option selector $this")
    }

}