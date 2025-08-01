package com.tezov.tuucho.core.domain.usecase

import com.tezov.tuucho.core.domain.model.Shadower
import com.tezov.tuucho.core.domain.protocol.ComponentRendererProtocol
import com.tezov.tuucho.core.domain.protocol.CoroutineScopeProviderProtocol
import com.tezov.tuucho.core.domain.protocol.RetrieveMaterialRepositoryProtocol
import com.tezov.tuucho.core.domain.protocol.ViewProtocol
import com.tezov.tuucho.core.domain.usecase.state.InitializeViewStateUseCase
import com.tezov.tuucho.core.domain.usecase.state.UpdateViewUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class RenderComponentUseCase(
    private val coroutineScopeProvider: CoroutineScopeProviderProtocol,
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
            coroutineScopeProvider.uiProcessor.launch {
                updateMaterialState.invoke(event.url, event.jsonObject)
            }
        }
        registerUpdateFormEvent.invoke { event ->
            coroutineScopeProvider.uiProcessor.launch {
                updateMaterialState.invoke(event.url, event.jsonObject)
            }
        }
    }

    suspend fun invoke(url: String): ViewProtocol? = coroutineScopeProvider.uiProcessor.async {
        initializeMaterialState.invoke(url)
        val component = retrieveMaterialRepository.process(url)
        screenRenderer.process(url, component)
    }.await()
}
