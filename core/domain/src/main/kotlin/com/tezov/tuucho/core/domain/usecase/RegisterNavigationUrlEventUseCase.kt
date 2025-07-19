package com.tezov.tuucho.core.domain.usecase

import com.tezov.tuucho.core.domain.actionHandler.NavigationUrlActionHandler
import com.tezov.tuucho.core.domain.protocol.CoroutineDispatchersProtocol
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class RegisterNavigationUrlEventUseCase(
    private val navigationUrlActionHandler: NavigationUrlActionHandler,
    private val coroutineDispatchers: CoroutineDispatchersProtocol,
) {

    fun invoke(
        onUrlRequested: (url: String) -> Unit,
    ) = navigationUrlActionHandler.events
        .onEach { onUrlRequested(it) }
        .launchIn(CoroutineScope(coroutineDispatchers.main))

}