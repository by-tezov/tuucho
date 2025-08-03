package com.tezov.tuucho.core.domain.business.usecase

import com.tezov.tuucho.core.domain.business.actionHandler.FormUpdateActionHandler
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol

class RegisterUpdateFormEventUseCase(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val formUpdateActionHandler: FormUpdateActionHandler,
) {

    fun invoke(
        onMessageReceived: (event: FormUpdateActionHandler.Event) -> Unit,
    ) {
        coroutineScopes.launchOnEvent {
            formUpdateActionHandler.events
                .forever { onMessageReceived(it) }
        }
    }

}