package com.tezov.tuucho.core.domain.usecase

import com.tezov.tuucho.core.domain.protocol.MaterialRepositoryProtocol
import com.tezov.tuucho.core.domain.protocol.ScreenProtocol
import com.tezov.tuucho.core.domain.protocol.ScreenRendererProtocol
import com.tezov.tuucho.core.domain.protocol.state.MaterialStateProtocol

class ComponentRenderUseCase(
    private val materialState: MaterialStateProtocol,
    private val repository: MaterialRepositoryProtocol,
    private val screenRenderer: ScreenRendererProtocol
) {

    suspend fun invoke(url: String): ScreenProtocol? {
        val component = repository.retrieve(url)
        materialState.clear()
        return screenRenderer.process(component)
    }

}