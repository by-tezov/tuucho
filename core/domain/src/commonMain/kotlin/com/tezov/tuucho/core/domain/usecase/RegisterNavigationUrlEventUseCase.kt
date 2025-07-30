package com.tezov.tuucho.core.domain.usecase

import com.tezov.tuucho.core.domain.actionHandler.NavigationUrlActionHandler
import com.tezov.tuucho.core.domain.protocol.CoroutineScopeProviderProtocol
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class RegisterNavigationUrlEventUseCase(
    private val coroutineScopeProvider: CoroutineScopeProviderProtocol,
    private val navigationUrlActionHandler: NavigationUrlActionHandler,
) {

    fun invoke(
        onUrlRequested: (url: String) -> Unit,
    ) {
        navigationUrlActionHandler.events
            .onEach { onUrlRequested(it) }
            .launchIn(coroutineScopeProvider.event)
    }

}