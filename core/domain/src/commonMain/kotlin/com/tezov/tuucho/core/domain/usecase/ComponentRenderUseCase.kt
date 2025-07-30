package com.tezov.tuucho.core.domain.usecase

import com.tezov.tuucho.core.domain.model.Shadower
import com.tezov.tuucho.core.domain.protocol.CoroutineContextProviderProtocol
import com.tezov.tuucho.core.domain.protocol.RetrieveMaterialRepositoryProtocol
import com.tezov.tuucho.core.domain.protocol.ScreenProtocol
import com.tezov.tuucho.core.domain.protocol.ScreenRendererProtocol
import com.tezov.tuucho.core.domain.protocol.state.MaterialStateProtocol
import kotlinx.coroutines.withContext

class ComponentRenderUseCase(
    private val coroutineDispatchers: CoroutineContextProviderProtocol,
    private val materialState: MaterialStateProtocol,
    private val repository: RetrieveMaterialRepositoryProtocol,
    private val screenRenderer: ScreenRendererProtocol,
    registerShadowerEvent: RegisterShadowerEventUseCase
) {

    init {
        registerShadowerEvent.invoke(
            Shadower.Type.onDemandDefinition
        ) { event ->
            if(event.url != materialState.url) return@invoke


            //TODO ok, now how I update the view ?

        }
    }

    suspend fun invoke(url: String): ScreenProtocol? = withContext(coroutineDispatchers.default) {
        materialState.clear() //TODO: current page is still visible by user, action on screen while this is fully processed can crash the app, need lock mecanisme
        materialState.url = url
        val component = repository.process(url)
        screenRenderer.process(component)
    }
}
