package com.tezov.tuucho.core.domain.usecase

import com.tezov.tuucho.core.domain.protocol.CoroutineContextProviderProtocol
import com.tezov.tuucho.core.domain.protocol.ShadowerMaterialRepositoryProtocol
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class RegisterShadowerEventUseCase(
    private val coroutineDispatchers: CoroutineContextProviderProtocol,
    private val shadowerMaterialRepository: ShadowerMaterialRepositoryProtocol,
) {

    fun invoke(
        type: String,
        onReceived: (event: ShadowerMaterialRepositoryProtocol.Event) -> Unit,
    ) {
        shadowerMaterialRepository.events
            .filter { it.type == type }
            .onEach { onReceived(it) }
            .launchIn(CoroutineScope(coroutineDispatchers.main))
    }

}