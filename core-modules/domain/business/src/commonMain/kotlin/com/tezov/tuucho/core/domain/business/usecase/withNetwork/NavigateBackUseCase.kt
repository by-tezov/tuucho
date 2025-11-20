package com.tezov.tuucho.core.domain.business.usecase.withNetwork

import com.tezov.tuucho.core.domain.business.di.TuuchoKoinComponent
import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.business.interaction.lock.InteractionLockGenerator.Lock
import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.jsonSchema._system.onScope
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.Shadower
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.component.ComponentSettingSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.component.SettingComponentShadowerSchema
import com.tezov.tuucho.core.domain.business.middleware.NavigationMiddleware
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol.Companion.execute
import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockRepositoryProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockRepositoryProtocol.Type
import com.tezov.tuucho.core.domain.business.protocol.repository.MaterialRepositoryProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.NavigationRepositoryProtocol
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.NavigateBackUseCase.Input
import com.tezov.tuucho.core.domain.tool.extension.ExtensionBoolean.isTrue

class NavigateBackUseCase(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val navigationStackRouteRepository: NavigationRepositoryProtocol.StackRoute,
    private val navigationStackScreenRepository: NavigationRepositoryProtocol.StackScreen,
    private val navigationStackTransitionRepository: NavigationRepositoryProtocol.StackTransition,
    private val shadowerMaterialRepository: MaterialRepositoryProtocol.Shadower,
    private val interactionLockRepository: InteractionLockRepositoryProtocol,
    private val navigationMiddlewares: List<NavigationMiddleware.Back>,
) : UseCaseProtocol.Sync<Input, Unit>,
    TuuchoKoinComponent {

    companion object {
        private const val requester = "NavigateBackUseCase"
    }

    data class Input(
        val lock: Lock.Element? = null
    )

    override fun invoke(
        input: Input
    ) {
        coroutineScopes.useCase.async {
            (navigationMiddlewares + terminalMiddleware()).execute(
                context = NavigationMiddleware.Back.Context(
                    currentUrl = navigationStackRouteRepository.currentRoute()?.value
                        ?: throw DomainException.Default("Shouldn't be possible"),
                    nextUrl = navigationStackRouteRepository.priorRoute()?.value,
                    input = input,
                    onShadowerException = null
                )
            )
        }
    }

    private fun terminalMiddleware() = NavigationMiddleware.Back { context, _ ->
        coroutineScopes.navigation.await {
            with(context.input) {
                val lock = lock.tryAcquire() ?: return@await
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
                interactionLockRepository.release(
                    requester = requester,
                    lock = lock
                )
            }
        }
    }

    private suspend fun Lock.Element?.tryAcquire(): Lock.Element? = if (this != null) {
        if (type != Type.Navigation) {
            throw DomainException.Default("expected lock of type Navigation but got $type")
        }
        takeIf { interactionLockRepository.isValid(it) }
    } else {
        interactionLockRepository.tryAcquire(
            requester = requester,
            type = Type.Navigation
        )
    }

    private suspend fun runShadower(
        route: NavigationRoute.Url,
        context: NavigationMiddleware.Back.Context
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
            ?.navigateBackward
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
                runCatching {
                    process()
                }.onFailure { failure ->
                    context.onShadowerException?.invoke(
                        failure,
                        context,
                        ::process
                    ) ?: throw failure
                }
            }
            if (settingShadowerScope?.waitDoneToRender.isTrue) {
                job.await()
            }
        }
    }
}
