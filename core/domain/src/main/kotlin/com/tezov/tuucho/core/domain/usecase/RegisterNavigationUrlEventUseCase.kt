package com.tezov.tuucho.core.domain.usecase

import com.tezov.tuucho.core.domain.actionHandler.NavigationUrlActionHandler
import kotlinx.coroutines.flow.onEach

class RegisterNavigationUrlEventUseCase(
    private val navigationUrlActionHandler: NavigationUrlActionHandler
) {

    fun invoke(
        onUrlRequested: (url: String) -> Unit
    ) = navigationUrlActionHandler.events
        .onEach { onUrlRequested(it) }

}