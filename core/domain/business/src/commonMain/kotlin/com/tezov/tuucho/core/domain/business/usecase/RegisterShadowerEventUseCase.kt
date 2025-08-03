package com.tezov.tuucho.core.domain.business.usecase

import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.ShadowerMaterialRepositoryProtocol

class RegisterShadowerEventUseCase(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val shadowerMaterialRepository: ShadowerMaterialRepositoryProtocol,
) {

    fun invoke(
        type: String,
        onReceived: suspend (event: ShadowerMaterialRepositoryProtocol.Event) -> Unit,
    ) {
        coroutineScopes.launchOnEvent {
            shadowerMaterialRepository.events
                .filter { it.type == type } //TODO fix that
                .forever { onReceived(it) }
        }
    }

}