package com.tezov.tuucho.core.domain.usecase

import com.tezov.tuucho.core.domain.protocol.MaterialRepositoryProtocol
import com.tezov.tuucho.core.domain.protocol.ScreenProtocol
import com.tezov.tuucho.core.domain.protocol.ScreenRendererProtocol

class ComponentRenderUseCase(
    private val repository: MaterialRepositoryProtocol,
    private val screenRenderer: ScreenRendererProtocol,
) {

    suspend fun invoke(url: String): ScreenProtocol? {
        val component = repository.retrieve(url)
        return screenRenderer.process(component)
    }

}