package com.tezov.tuucho.core.domain.business.usecase

import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.interaction.navigation.selector.PageBreadCrumbNavigationDefinitionSelectorMatcher
import com.tezov.tuucho.core.domain.business.jsonSchema._system.onScope
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.Shadower
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.component.ComponentSettingSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.component.SettingComponentShadowerSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.component.navigationSchema.ComponentSettingNavigationSchema
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.IdGeneratorProtocol
import com.tezov.tuucho.core.domain.business.protocol.NavigationDefinitionSelectorMatcherProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.ActionLockRepositoryProtocol
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
    private val navigationRouteIdGenerator: IdGeneratorProtocol,
    private val navigationOptionSelectorFactory: NavigationDefinitionSelectorMatcherFactoryUseCase,
    private val navigationStackRouteRepository: NavigationRepositoryProtocol.StackRoute,
    private val navigationStackScreenRepository: NavigationRepositoryProtocol.StackScreen,
    private val navigationStackTransitionRepository: NavigationRepositoryProtocol.StackTransition,
    private val shadowerMaterialRepository: MaterialRepositoryProtocol.Shadower,
    private val actionLockRepository: ActionLockRepositoryProtocol,
) : UseCaseProtocol.Sync<Input, Unit> {

    data class Input(
        val url: String,
    )

    override fun invoke(input: Input) {
        coroutineScopes.navigation.async {
            val interactionHandle = actionLockRepository
                .tryLock(ActionLockRepositoryProtocol.Type.Navigation)
                ?: return@async
            with(input) {
                val componentObject = retrieveMaterialRepository.process(url)
                val navigationSettingObject = componentObject
                    .onScope(ComponentSettingSchema.Root::Scope)
                    .navigation
                val navigationDefinitionObject = navigationSettingObject
                    ?.withScope(ComponentSettingNavigationSchema::Scope)
                    ?.definition?.navigationResolver()
                val newRoute = navigationStackRouteRepository.forward(
                    route = NavigationRoute.Url(navigationRouteIdGenerator.generate(), url),
                    navigationOptionObject = navigationDefinitionObject
                        ?.withScope(ComponentSettingNavigationSchema.Definition::Scope)?.option
                )
                newRoute?.let {
                    navigationStackScreenRepository.forward(
                        route = newRoute,
                        componentObject = componentObject
                    )
                    newRoute.runShadower()
                }
                navigationStackTransitionRepository.forward(
                    routes = navigationStackRouteRepository.routes(),
                    navigationExtraObject = navigationSettingObject
                        ?.withScope(ComponentSettingNavigationSchema::Scope)
                        ?.extra,
                    navigationTransitionObject = navigationDefinitionObject
                        ?.withScope(ComponentSettingNavigationSchema.Definition::Scope)?.transition,
                )
                actionLockRepository.unLock(
                    ActionLockRepositoryProtocol.Type.Navigation,
                    interactionHandle
                )
            }
        }
    }

    private suspend fun JsonArray?.navigationResolver() = this
        ?.firstOrNull { it.jsonObject.accept() }
        ?.let { it as? JsonObject }

    private suspend fun JsonObject.accept(): Boolean {
        val selector =
            withScope(ComponentSettingNavigationSchema.Definition::Scope).selector ?: return true
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

    private suspend fun NavigationRoute.runShadower() {
        val url = (this as? NavigationRoute.Url)?.value ?: return
        val view = navigationStackScreenRepository.getScreenOrNull(this)?.view ?: return
        val componentObject = view.componentObject
        val componentSettingScope = componentObject
            .onScope(ComponentSettingSchema.Root::Scope)
        val settingShadowerScope = componentSettingScope
            .contextualShadower
            ?.withScope(SettingComponentShadowerSchema::Scope)
            ?.navigateForward
            ?.withScope(SettingComponentShadowerSchema.Navigate::Scope)
        if (settingShadowerScope?.enable.isTrue) {
            val job = coroutineScopes.navigation.async {
                shadowerMaterialRepository.process(
                    url = url,
                    materialObject = componentObject,
                    types = listOf(Shadower.Type.contextual)
                ).forEach {
                    coroutineScopes.renderer.await {
                        view.update(it.jsonObject)
                    }
                }
            }
            if (settingShadowerScope?.waitDoneToRender.isTrue) {
                job.await()
            }
        }
    }

}