package com.tezov.tuucho.core.domain.business.usecase

import com.tezov.tuucho.core.domain.business.actionHandler.NavigationUrlActionHandler
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol

class InitiateAndRegisterToNavigationEventUseCase(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val navigationUrlActionHandler: NavigationUrlActionHandler,
    private val navigateForward: NavigateForwardUseCase,
) {

    fun invoke(url: String, triggerUpdate: () -> Unit) {
        coroutineScopes.launchOnEvent {
            navigationUrlActionHandler.events
                .forever { navigate(url, triggerUpdate) }
        }
        coroutineScopes.launchOnEvent {
            navigate(url, triggerUpdate)
        }
    }

    private suspend fun navigate(url: String, triggerUpdate: () -> Unit) {
        navigateForward.invoke(url)
        triggerUpdate.invoke()
    }

}