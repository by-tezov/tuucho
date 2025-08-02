package com.tezov.tuucho.core.domain.usecase

import com.tezov.tuucho.core.domain.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.protocol.ShadowerMaterialRepositoryProtocol
import kotlinx.coroutines.flow.filter

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
                .filter { it.type == type }
                .collect { onReceived(it) }
        }
    }

}