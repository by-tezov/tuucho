package com.tezov.tuucho.core.domain.business.usecase.withNetwork

import com.tezov.tuucho.core.domain.business.di.TuuchoKoinComponent
import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.interaction.navigation.selector.PageBreadCrumbNavigationDefinitionSelectorMatcher
import com.tezov.tuucho.core.domain.business.jsonSchema._system.onScope
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.Shadower
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.component.ComponentSettingSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.component.SettingComponentShadowerSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.component.navigationSchema.ComponentSettingNavigationSchema
import com.tezov.tuucho.core.domain.business.middleware.MiddlewareProtocol.Companion.execute
import com.tezov.tuucho.core.domain.business.middleware.NavigationMiddleware
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.IdGeneratorProtocol
import com.tezov.tuucho.core.domain.business.protocol.NavigationDefinitionSelectorMatcherProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockRepositoryProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.MaterialRepositoryProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.NavigationRepositoryProtocol
import com.tezov.tuucho.core.domain.business.usecase._system.UseCaseExecutor
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.NavigateToUrlUseCase.Input
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.NavigationDefinitionSelectorMatcherFactoryUseCase
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
    private val actionLockRepository: InteractionLockRepositoryProtocol,
    private val navigationMiddlewares: List<NavigationMiddleware.ToUrl>
) : UseCaseProtocol.Sync<Input, Unit>,
    TuuchoKoinComponent {
    data class Input(
        val url: String,
    )

    override fun invoke(
        input: Input
    ) {
        coroutineScopes.useCase.async {
            (navigationMiddlewares + finalNavigationMiddleware()).execute(
                context = NavigationMiddleware.ToUrl.Context(
                    currentUrl = navigationStackRouteRepository.currentRoute()?.value,
                    input = input,
                    onShadowerException = null
                )
            )
        }
    }

    private fun finalNavigationMiddleware(): NavigationMiddleware.ToUrl = NavigationMiddleware.ToUrl { context, next ->
        coroutineScopes.navigation.await {
            val interactionHandle = tryLock() ?: return@await
            with(context.input) {
                val componentObject = runCatching {
                    retrieveMaterialRepository.process(url)
                }.onFailure {
                    interactionHandle.unLock()
                }.getOrThrow()
                val navigationSettingObject = componentObject
                    .onScope(ComponentSettingSchema.Root::Scope)
                    .navigation
                val navigationDefinitionObject = navigationSettingObject
                    ?.withScope(ComponentSettingNavigationSchema::Scope)
                    ?.definition
                    ?.navigationResolver()
                val newRoute = navigationStackRouteRepository.forward(
                    route = NavigationRoute.Url(navigationRouteIdGenerator.generate(), url),
                    navigationOptionObject = navigationDefinitionObject
                        ?.withScope(ComponentSettingNavigationSchema.Definition::Scope)
                        ?.option
                )
                newRoute?.let {
                    navigationStackScreenRepository.forward(
                        route = newRoute,
                        componentObject = componentObject
                    )
                    runShadower(newRoute, context)
                }
                navigationStackTransitionRepository.forward(
                    routes = navigationStackRouteRepository.routes(),
                    navigationExtraObject = navigationSettingObject
                        ?.withScope(ComponentSettingNavigationSchema::Scope)
                        ?.extra,
                    navigationTransitionObject = navigationDefinitionObject
                        ?.withScope(ComponentSettingNavigationSchema.Definition::Scope)
                        ?.transition,
                )
            }
            interactionHandle.unLock()
        }
    }

    private suspend fun tryLock() = actionLockRepository
        .tryLock(InteractionLockRepositoryProtocol.Type.Navigation)

    private suspend fun String.unLock() {
        actionLockRepository.unLock(
            InteractionLockRepositoryProtocol.Type.Navigation,
            this
        )
    }

    private suspend fun JsonArray?.navigationResolver() = this
        ?.firstOrNull { it.jsonObject.accept() }
        ?.let { it as? JsonObject }

    private suspend fun JsonObject.accept(): Boolean {
        val selector =
            withScope(ComponentSettingNavigationSchema.Definition::Scope).selector ?: return true
        return useCaseExecutor
            .invokeSuspend(
                useCase = navigationOptionSelectorFactory,
                input = NavigationDefinitionSelectorMatcherFactoryUseCase.Input(
                    prototypeObject = selector
                )
            ).selector
            .accept()
    }

    private suspend fun NavigationDefinitionSelectorMatcherProtocol.accept() = when (this) {
        is PageBreadCrumbNavigationDefinitionSelectorMatcher -> {
            val route = navigationStackRouteRepository.routes()
            accept(route.map { it.value })
        }

        else -> {
            throw DomainException.Default("Unknown navigation option selector $this")
        }
    }

    private suspend fun runShadower(
        route: NavigationRoute.Url,
        context: NavigationMiddleware.ToUrl.Context
    ) {
        val view = navigationStackScreenRepository
            .getScreenOrNull(route)
            ?.view
            ?: return
        val componentObject = view.componentObject
        val componentSettingScope = componentObject
            .onScope(ComponentSettingSchema.Root::Scope)
        val settingShadowerScope = componentSettingScope
            .shadower
            ?.withScope(SettingComponentShadowerSchema::Scope)
            ?.navigateForward
            ?.withScope(SettingComponentShadowerSchema.Navigate::Scope)
        if (settingShadowerScope?.enable.isTrue) {
            val job = coroutineScopes.navigation.async {
                suspend fun process() {
                    shadowerMaterialRepository
                        .process(
                            url = route.value,
                            componentObject = componentObject,
                            types = listOf(Shadower.Type.contextual)
                        ).forEach {
                            coroutineScopes.renderer.await {
                                view.update(it.jsonObject)
                            }
                        }
                }
                runCatching { process() }.onFailure { failure ->
                    context.onShadowerException?.invoke(failure, context) {
                        process()
                    } ?: throw failure
                }
            }
            if (settingShadowerScope?.waitDoneToRender.isTrue) {
                job.await()
            }
        }
    }
}
