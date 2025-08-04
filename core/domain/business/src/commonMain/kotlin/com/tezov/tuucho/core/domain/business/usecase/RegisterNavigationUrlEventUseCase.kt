package com.tezov.tuucho.core.domain.business.usecase

import com.tezov.tuucho.core.domain.business.actionHandler.NavigationUrlActionHandler
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol

class RegisterNavigationUrlEventUseCase(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val navigationUrlActionHandler: NavigationUrlActionHandler,
) {

    fun invoke(
        onUrlRequested: (url: String) -> Unit,
    ) {
        coroutineScopes.launchOnEvent {
            navigationUrlActionHandler.events
                .forever { onUrlRequested(it) }
        }
    }

}