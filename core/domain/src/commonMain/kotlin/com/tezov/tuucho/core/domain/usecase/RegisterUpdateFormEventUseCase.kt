package com.tezov.tuucho.core.domain.usecase

import com.tezov.tuucho.core.domain.actionHandler.FormUpdateActionHandler
import com.tezov.tuucho.core.domain.protocol.CoroutineScopesProtocol

class RegisterUpdateFormEventUseCase(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val formUpdateActionHandler: FormUpdateActionHandler,
) {

    fun invoke(
        onMessageReceived: (event: FormUpdateActionHandler.Event) -> Unit,
    ) {
        coroutineScopes.launchOnEvent {
            formUpdateActionHandler.events
                .collect { onMessageReceived(it) }
        }
    }

}