package com.tezov.tuucho.core.domain.business.usecase

import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.business.navigation.ViewContext
import com.tezov.tuucho.core.domain.business.protocol.ComponentRendererProtocol
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import com.tezov.tuucho.core.domain.business.protocol.state.StateViewProtocol
import com.tezov.tuucho.core.domain.business.usecase.RenderViewContextUseCase.Input
import com.tezov.tuucho.core.domain.business.usecase.RenderViewContextUseCase.Output
import kotlinx.serialization.json.JsonObject

class RenderViewContextUseCase(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val stateViewFactory: () -> StateViewProtocol,
    private val componentRenderer: ComponentRendererProtocol,
) : UseCaseProtocol.Async<Input, Output> {

    data class Input(
        val url: String,
        val componentObject: JsonObject,
    )

    data class Output(
        val viewContext: ViewContext,
    )

    override suspend fun invoke(input: Input) = with(input) {
        Output(
            viewContext = coroutineScopes.onUiProcessor {
                ViewContext(
                    url = url,
                    view = lazy {
                        componentRenderer.process(url, componentObject)
                            ?: throw DomainException.Default("Failed to render the view $url")
                    },
                    state = stateViewFactory.invoke(),
                    //TODO animation
                )
            }
        )
    }

}
