package com.tezov.tuucho.core.domain.business.usecase.withNetwork

import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.jsonSchema._system.onScope
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.Shadower
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.component.ComponentSettingSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.component.SettingComponentShadowerSchema
import com.tezov.tuucho.core.domain.business.middleware.MiddlewareProtocol.Companion.execute
import com.tezov.tuucho.core.domain.business.middleware.NavigationMiddleware
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.InterractionLockRepositoryProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.MaterialRepositoryProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.NavigationRepositoryProtocol
import com.tezov.tuucho.core.domain.tool.extension.ExtensionBoolean.isTrue
import org.koin.core.component.KoinComponent

class NavigateBackUseCase(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val navigationStackRouteRepository: NavigationRepositoryProtocol.StackRoute,
    private val navigationStackScreenRepository: NavigationRepositoryProtocol.StackScreen,
    private val navigationStackTransitionRepository: NavigationRepositoryProtocol.StackTransition,
    private val shadowerMaterialRepository: MaterialRepositoryProtocol.Shadower,
    private val actionLockRepository: InterractionLockRepositoryProtocol,
    private val navigationMiddlewares: List<NavigationMiddleware.Back>,
) : UseCaseProtocol.Sync<Unit, Unit>, KoinComponent {

    override fun invoke(input: Unit) {
        coroutineScopes.useCase.async {
            (navigationMiddlewares + finalNavigationMiddleware()).execute(
                context = NavigationMiddleware.Back.Context(
                    currentUrl = navigationStackRouteRepository.currentRoute()?.value
                        ?: throw DomainException.Default("Shouldn't be possible"),
                    nextUrl = navigationStackRouteRepository.priorRoute()?.value,
                    onShadowerException = null
                )
            )
        }
    }

    private suspend fun tryLock() = actionLockRepository
        .tryLock(InterractionLockRepositoryProtocol.Type.Navigation)

    private suspend fun String.unLock() {
        actionLockRepository.unLock(
            InterractionLockRepositoryProtocol.Type.Navigation,
            this
        )
    }

    private fun finalNavigationMiddleware() = NavigationMiddleware.Back { context, next ->
        coroutineScopes.navigation.await {
            val interactionHandle = tryLock() ?: return@await
            val restoredRoute = navigationStackRouteRepository.backward(
                route = NavigationRoute.Back
            )
            restoredRoute?.let { runShadower(restoredRoute, context) }
            navigationStackTransitionRepository.backward(
                routes = navigationStackRouteRepository.routes(),
            )
            navigationStackScreenRepository.backward(
                routes = navigationStackRouteRepository.routes(),
            )
            interactionHandle.unLock()
        }
    }

    private suspend fun runShadower(
        route: NavigationRoute.Url,
        context: NavigationMiddleware.Back.Context
    ) {
        val view = navigationStackScreenRepository
            .getScreenOrNull(route)?.view
            ?: return
        val componentObject = view.componentObject
        val componentSettingScope = componentObject
            .onScope(ComponentSettingSchema.Root::Scope)
        val settingShadowerScope = componentSettingScope
            .shadower
            ?.withScope(SettingComponentShadowerSchema::Scope)
            ?.navigateBackward
            ?.withScope(SettingComponentShadowerSchema.Navigate::Scope)
        if (settingShadowerScope?.enable.isTrue) {
            val job = coroutineScopes.navigation.async {
                suspend fun process() {
                    shadowerMaterialRepository.process(
                        url = route.value,
                        componentObject = componentObject,
                        types = listOf(Shadower.Type.contextual)
                    ).forEach {
                        coroutineScopes.renderer.await {
                            view.update(it.jsonObject)
                        }
                    }
                }
                runCatching {
                    process()
                }.onFailure { failure ->
                    context.onShadowerException?.invoke(failure, context) {
                        process()
                    } ?:throw failure
                }
            }
            if (settingShadowerScope?.waitDoneToRender.isTrue) {
                job.await()
            }
        }
    }
}