package com.tezov.tuucho.core.domain.business.usecase.withNetwork

import com.tezov.tuucho.core.domain.business._system.koin.TuuchoKoinComponent
import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRouteIdGenerator
import com.tezov.tuucho.core.domain.business.interaction.navigation.selector.PageBreadCrumbNavigationDefinitionSelectorMatcher
import com.tezov.tuucho.core.domain.business.jsonSchema._system.onScope
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.Shadower
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.component.ComponentSettingSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.component.SettingComponentShadowerSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.component.navigationSchema.ComponentSettingNavigationSchema
import com.tezov.tuucho.core.domain.business.middleware.NavigationMiddleware
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareExecutorProtocol
import com.tezov.tuucho.core.domain.business.protocol.NavigationDefinitionSelectorMatcherProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseExecutorProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.MaterialRepositoryProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.NavigationRepositoryProtocol
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.NavigateToUrlUseCase.Input
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.NavigationDefinitionSelectorMatcherFactoryUseCase
import com.tezov.tuucho.core.domain.test._system.OpenForTest
import com.tezov.tuucho.core.domain.tool.extension.ExtensionBoolean.isTrue
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject

@OpenForTest
class NavigateToUrlUseCase(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val useCaseExecutor: UseCaseExecutorProtocol,
    private val retrieveMaterialRepository: MaterialRepositoryProtocol.Retrieve,
    private val navigationRouteIdGenerator: NavigationRouteIdGenerator,
    private val navigationOptionSelectorFactory: NavigationDefinitionSelectorMatcherFactoryUseCase,
    private val navigationStackRouteRepository: NavigationRepositoryProtocol.StackRoute,
    private val navigationStackScreenRepository: NavigationRepositoryProtocol.StackScreen,
    private val navigationStackTransitionRepository: NavigationRepositoryProtocol.StackTransition,
    private val shadowerMaterialRepository: MaterialRepositoryProtocol.Shadower,
    private val middlewareExecutor: MiddlewareExecutorProtocol,
    private val navigationMiddlewares: List<NavigationMiddleware.ToUrl>
) : UseCaseProtocol.Async<Input, Unit>,
    TuuchoKoinComponent {
    data class Input(
        val url: String
    )

    override suspend fun invoke(
        input: Input
    ) {
        coroutineScopes.useCase.await {
            middlewareExecutor.process(
                middlewares = navigationMiddlewares + terminalMiddleware(),
                context = NavigationMiddleware.ToUrl.Context(
                    currentUrl = navigationStackRouteRepository.currentRoute()?.value,
                    input = input,
                    onShadowerException = null
                )
            )
        }
    }

    private fun terminalMiddleware(): NavigationMiddleware.ToUrl = NavigationMiddleware.ToUrl { context, _ ->
        coroutineScopes.navigation.await {
            with(context.input) {
                val componentObject = retrieveMaterialRepository.process(url)
                val navigationSettingObject = componentObject
                    .onScope(ComponentSettingSchema.Root::Scope)
                    .navigation
                val navigationDefinitionObject = navigationSettingObject
                    ?.withScope(ComponentSettingNavigationSchema::Scope)
                    ?.definitions
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
                    runShadower(newRoute, componentObject, context)
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
        }
    }

    private suspend fun JsonArray?.navigationResolver() = this
        ?.firstOrNull { it.jsonObject.accept() }
        ?.let { it as? JsonObject }

    private suspend fun JsonObject.accept(): Boolean {
        val selector =
            withScope(ComponentSettingNavigationSchema.Definition::Scope).selector ?: return true
        return useCaseExecutor
            .await(
                useCase = navigationOptionSelectorFactory,
                input = NavigationDefinitionSelectorMatcherFactoryUseCase.Input(
                    prototypeObject = selector
                )
            )?.selector
            ?.accept() ?: throw DomainException.Default("Should not be possible")
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
        componentObject: JsonObject,
        context: NavigationMiddleware.ToUrl.Context
    ) {
        val screen = navigationStackScreenRepository
            .getScreenOrNull(route)
            ?: return
        val componentSettingScope = componentObject
            .onScope(ComponentSettingSchema.Root::Scope)
        val settingShadowerScope = componentSettingScope
            .shadower
            ?.withScope(SettingComponentShadowerSchema::Scope)
            ?.navigateForward
            ?.withScope(SettingComponentShadowerSchema.Navigate::Scope)
        if (settingShadowerScope?.enable.isTrue) {
            val job = coroutineScopes.navigation.async(
                throwOnFailure = false
            ) {
                suspend fun process() {
                    val jsonObjects = shadowerMaterialRepository
                        .process(
                            url = route.value,
                            componentObject = componentObject,
                            types = listOf(Shadower.Type.contextual)
                        ).map { it.jsonObject }
                    coroutineScopes.renderer.await {
                        screen.update(jsonObjects)
                    }
                }
                runCatching { process() }.onFailure { failure ->
                    context.onShadowerException?.process(
                        exception = failure,
                        context = context,
                        replay = ::process
                    ) ?: throw failure
                }
            }
            if (settingShadowerScope?.waitDoneToRender.isTrue) {
                job.await()
            } else {
                coroutineScopes.navigation.throwOnFailure(job)
            }
        }
    }
}
