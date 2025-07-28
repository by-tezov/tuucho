package com.tezov.tuucho.core.domain.usecase

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
    private val screenRenderer: ScreenRendererProtocol
) {

    suspend fun invoke(url: String): ScreenProtocol? = withContext(coroutineDispatchers.default) {
        val component = repository.retrieve(url)
        materialState.clear()
        screenRenderer.process(component)
    }
}
