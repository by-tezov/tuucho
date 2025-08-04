package com.tezov.tuucho.core.domain.business.usecase

import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.business.navigation.ViewContext
import com.tezov.tuucho.core.domain.business.protocol.ComponentRendererProtocol
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.state.StateViewProtocol
import kotlinx.serialization.json.JsonObject

class RenderViewContextUseCase(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val stateViewFactory: () -> StateViewProtocol,
    private val componentRenderer: ComponentRendererProtocol,
) {

    suspend fun invoke(url: String, component: JsonObject) = coroutineScopes.onUiProcessor {
        ViewContext(
            url = url,
            view = componentRenderer.process(url, component)
                ?: throw DomainException.Default("Failed to render the view $url"),
            state = stateViewFactory.invoke(),
            //TODO animation
        )
    }

}
