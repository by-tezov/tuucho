package com.tezov.tuucho.core.domain.usecase

import com.tezov.tuucho.core.domain.protocol.CoroutineScopeProviderProtocol
import com.tezov.tuucho.core.domain.protocol.ShadowerMaterialRepositoryProtocol
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class RegisterShadowerEventUseCase(
    private val coroutineScopeProvider: CoroutineScopeProviderProtocol,
    private val shadowerMaterialRepository: ShadowerMaterialRepositoryProtocol,
) {

    fun invoke(
        type: String,
        onReceived: suspend (event: ShadowerMaterialRepositoryProtocol.Event) -> Unit,
    ) {
        shadowerMaterialRepository.events
            .filter { it.type == type }
            .onEach { onReceived(it) }
            .launchIn(coroutineScopeProvider.event)
    }

}