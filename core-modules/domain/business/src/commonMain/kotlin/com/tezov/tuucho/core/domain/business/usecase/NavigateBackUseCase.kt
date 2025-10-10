package com.tezov.tuucho.core.domain.business.usecase

import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.jsonSchema._system.onScope
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.Shadower
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.component.ComponentSettingSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.component.SettingComponentShadowerSchema
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.HookProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.ActionLockRepositoryProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.MaterialRepositoryProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.NavigationRepositoryProtocol
import com.tezov.tuucho.core.domain.tool.extension.ExtensionBoolean.isFalse
import com.tezov.tuucho.core.domain.tool.extension.ExtensionBoolean.isTrue

class NavigateBackUseCase(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val navigationStackRouteRepository: NavigationRepositoryProtocol.StackRoute,
    private val navigationStackScreenRepository: NavigationRepositoryProtocol.StackScreen,
    private val navigationStackTransitionRepository: NavigationRepositoryProtocol.StackTransition,
    private val shadowerMaterialRepository: MaterialRepositoryProtocol.Shadower,
    private val actionLockRepository: ActionLockRepositoryProtocol,
    private val hookBeforeNavigation: HookProtocol.BeforeNavigateBack?,
    private val hookAfterNavigation: HookProtocol.AfterNavigateBack?,
) : UseCaseProtocol.Sync<Unit, Unit> {

    override fun invoke(input: Unit) {
        coroutineScopes.navigation.async {
            val interactionHandle = actionLockRepository
                .tryLock(ActionLockRepositoryProtocol.Type.Navigation)
                ?: return@async
            val result = hookBeforeNavigation?.onEvent(
                currentUrl = (navigationStackRouteRepository.currentRoute() as? NavigationRoute.Url)?.value
                    ?: "",
                nextUrl = (navigationStackRouteRepository.priorRoute() as? NavigationRoute.Url)?.value
            )
            if (result.isFalse) {
                actionLockRepository.unLock(
                    ActionLockRepositoryProtocol.Type.Navigation,
                    interactionHandle
                )
                return@async
            }
            val restoredRoute = navigationStackRouteRepository.backward(
                route = NavigationRoute.Back
            )
            restoredRoute?.runShadower()
            navigationStackTransitionRepository.backward(
                routes = navigationStackRouteRepository.routes(),
            )
            navigationStackScreenRepository.backward(
                routes = navigationStackRouteRepository.routes(),
            )
            actionLockRepository.unLock(
                ActionLockRepositoryProtocol.Type.Navigation,
                interactionHandle
            )
            hookAfterNavigation?.onEvent(
                (navigationStackRouteRepository.currentRoute() as? NavigationRoute.Url)?.value
            )
        }
    }

    private suspend fun NavigationRoute.runShadower() {
        val url = (this as? NavigationRoute.Url)?.value ?: return
        val view = navigationStackScreenRepository.getScreenOrNull(this)?.view ?: return
        val componentObject = view.componentObject
        val componentSettingScope = componentObject.onScope(ComponentSettingSchema.Root::Scope)
        val settingShadowerScope = componentSettingScope
            .shadower
            ?.withScope(SettingComponentShadowerSchema::Scope)
            ?.navigateBackward
            ?.withScope(SettingComponentShadowerSchema.Navigate::Scope)
        if (settingShadowerScope?.enable.isTrue) {
            val job = coroutineScopes.navigation.async {
                shadowerMaterialRepository.process(
                    url = url,
                    componentObject = componentObject,
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