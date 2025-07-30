package com.tezov.tuucho.core.domain.usecase

import com.tezov.tuucho.core.domain.model.Shadower
import com.tezov.tuucho.core.domain.protocol.CoroutineScopeProviderProtocol
import com.tezov.tuucho.core.domain.protocol.RetrieveMaterialRepositoryProtocol
import com.tezov.tuucho.core.domain.protocol.ScreenProtocol
import com.tezov.tuucho.core.domain.protocol.ScreenRendererProtocol
import com.tezov.tuucho.core.domain.usecase.state.InitializeMaterialStateUseCase
import com.tezov.tuucho.core.domain.usecase.state.UpdateMaterialStateUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class ComponentRenderUseCase(
    private val coroutineScopeProvider: CoroutineScopeProviderProtocol,
    private val initializeMaterialState: InitializeMaterialStateUseCase,
    private val updateMaterialState: UpdateMaterialStateUseCase,
    private val retrieveMaterialRepository: RetrieveMaterialRepositoryProtocol,
    private val screenRenderer: ScreenRendererProtocol,
    registerShadowerEvent: RegisterShadowerEventUseCase,
    registerUpdateFormEvent: RegisterUpdateFormEventUseCase,
) {

    init {
        registerShadowerEvent.invoke(
            Shadower.Type.onDemandDefinition
        ) { event ->
            coroutineScopeProvider.uiProcessor.launch {
                val delay = Random.nextLong() % 1500 + 1500
                println("$delay $event")
                delay(delay) //TODO remove
                updateMaterialState.invoke(event.url, event.jsonObject)
            }
        }
        registerUpdateFormEvent.invoke { event ->
            coroutineScopeProvider.uiProcessor.launch {
                updateMaterialState.invoke(event.url, event.jsonObject)
            }
        }
    }

    suspend fun invoke(url: String): ScreenProtocol? = coroutineScopeProvider.uiProcessor.async {
        initializeMaterialState.invoke(url)
        val component = retrieveMaterialRepository.process(url)
        screenRenderer.process(url, component)
    }.await()
}
