package com.tezov.tuucho.core.domain.usecase

import com.tezov.tuucho.core.domain.actionHandler.FormUpdateActionHandler
import com.tezov.tuucho.core.domain.protocol.CoroutineScopeProviderProtocol
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class RegisterUpdateFormEventUseCase(
    private val coroutineScopeProvider: CoroutineScopeProviderProtocol,
    private val formUpdateActionHandler: FormUpdateActionHandler,
) {

    fun invoke(
        onMessageReceived: (event: FormUpdateActionHandler.Event) -> Unit,
    ) {
        formUpdateActionHandler.events
            .onEach { onMessageReceived(it) }
            .launchIn(coroutineScopeProvider.event)
    }

}