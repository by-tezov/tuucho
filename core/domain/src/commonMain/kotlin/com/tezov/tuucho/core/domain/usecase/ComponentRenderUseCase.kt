package com.tezov.tuucho.core.domain.usecase

import com.tezov.tuucho.core.domain.model.Shadower
import com.tezov.tuucho.core.domain.protocol.CoroutineContextProviderProtocol
import com.tezov.tuucho.core.domain.protocol.RetrieveMaterialRepositoryProtocol
import com.tezov.tuucho.core.domain.protocol.ScreenProtocol
import com.tezov.tuucho.core.domain.protocol.ScreenRendererProtocol
import com.tezov.tuucho.core.domain.protocol.state.MaterialStateProtocol
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

class ComponentRenderUseCase(
    private val coroutineContextProvider: CoroutineContextProviderProtocol,
    private val materialState: MaterialStateProtocol,
    private val repository: RetrieveMaterialRepositoryProtocol,
    private val screenRenderer: ScreenRendererProtocol,
    registerShadowerEvent: RegisterShadowerEventUseCase
) {

    private val coroutineScope = CoroutineScope(coroutineContextProvider.main)

    init {
        registerShadowerEvent.invoke(
            Shadower.Type.onDemandDefinition
        ) { event ->
            coroutineScope.launch {
                delay( Random.nextLong() % 2000 + 500) //TODO remove
                materialState.update(event.url, event.jsonObject)
            }
        }
    }

    suspend fun invoke(url: String): ScreenProtocol? = withContext(coroutineContextProvider.default) {
        materialState.clear() //TODO: current page is still visible by user, action on screen while this is fully processed can crash the app, need lock mecanisme
        materialState.url = url
        val component = repository.process(url)
        screenRenderer.process(component)
    }
}
