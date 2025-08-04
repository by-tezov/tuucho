package com.tezov.tuucho.core.domain.business.usecase

import com.tezov.tuucho.core.domain.business.model.Shadower
import com.tezov.tuucho.core.domain.business.protocol.ComponentRendererProtocol
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.RetrieveMaterialRepositoryProtocol
import com.tezov.tuucho.core.domain.business.protocol.ViewProtocol
import com.tezov.tuucho.core.domain.business.usecase.state.InitializeViewStateUseCase
import com.tezov.tuucho.core.domain.business.usecase.state.UpdateViewUseCase

class RenderComponentUseCase(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val initializeMaterialState: InitializeViewStateUseCase,
    private val updateMaterialState: UpdateViewUseCase,
    private val retrieveMaterialRepository: RetrieveMaterialRepositoryProtocol,
    private val screenRenderer: ComponentRendererProtocol,
    registerShadowerEvent: RegisterShadowerEventUseCase,
    registerUpdateFormEvent: RegisterUpdateFormEventUseCase,
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

    suspend fun invoke(url: String): ViewProtocol? = coroutineScopes.onUiProcessor {
        initializeMaterialState.invoke(url)
        val component = retrieveMaterialRepository.process(url)
        screenRenderer.process(url, component)
    }
}
