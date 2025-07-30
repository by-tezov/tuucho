package com.tezov.tuucho.core.domain.usecase

import com.tezov.tuucho.core.domain.actionHandler.FormUpdateActionHandler
import com.tezov.tuucho.core.domain.protocol.CoroutineContextProviderProtocol
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class RegisterUpdateFormEventUseCase(
    private val coroutineContextProvider: CoroutineContextProviderProtocol,
    private val formUpdateActionHandler: FormUpdateActionHandler,
) {

    fun invoke(
        onAuthorityRequested: (event: FormUpdateActionHandler.Event) -> Unit,
    ) {
        formUpdateActionHandler.events
            .onEach { onAuthorityRequested(it) }
            .launchIn(CoroutineScope(coroutineContextProvider.main))
    }

}