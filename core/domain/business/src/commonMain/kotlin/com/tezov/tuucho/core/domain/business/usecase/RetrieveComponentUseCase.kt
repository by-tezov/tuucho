package com.tezov.tuucho.core.domain.business.usecase

import com.tezov.tuucho.core.domain.business.model.schema.material.Shadower
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.RetrieveMaterialRepositoryProtocol
import com.tezov.tuucho.core.domain.business.usecase.state.UpdateViewUseCase

class RetrieveComponentUseCase(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val updateMaterialState: UpdateViewUseCase,
    private val retrieveMaterialRepository: RetrieveMaterialRepositoryProtocol,
    registerShadowerEvent: RegisterToShadowerEventUseCase,
    registerUpdateFormEvent: RegisterToFormUpdateEventUseCase,
) {

    init {
        registerShadowerEvent.invoke(
            Shadower.Type.onDemandDefinition
        ) { event ->
            coroutineScopes.launchOnUiProcessor {
                updateMaterialState.invoke(event.url, event.jsonObject)
            }
        }
        registerUpdateFormEvent.invoke { event ->
            coroutineScopes.launchOnUiProcessor {
                updateMaterialState.invoke(event.url, event.jsonObject)
            }
        }
    }

    suspend fun invoke(url: String) = retrieveMaterialRepository.process(url)

}
