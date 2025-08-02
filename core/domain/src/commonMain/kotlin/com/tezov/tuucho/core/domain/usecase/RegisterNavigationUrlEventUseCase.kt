package com.tezov.tuucho.core.domain.usecase

import com.tezov.tuucho.core.domain.actionHandler.NavigationUrlActionHandler
import com.tezov.tuucho.core.domain.protocol.CoroutineScopesProtocol

class RegisterNavigationUrlEventUseCase(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val navigationUrlActionHandler: NavigationUrlActionHandler,
) {

    fun invoke(
        onUrlRequested: (url: String) -> Unit,
    ) {
        coroutineScopes.launchOnEvent {
            navigationUrlActionHandler.events
                .collect { onUrlRequested(it) }
        }
    }

}