package com.tezov.tuucho.core.domain.business.usecase

import com.tezov.tuucho.core.domain.business.model.ActionModelDomain
import com.tezov.tuucho.core.domain.business.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.protocol.ActionProcessorProtocol
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import kotlinx.serialization.json.JsonElement

class ProcessActionUseCase(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val handlers: List<ActionProcessorProtocol>,
) : UseCaseProtocol.Async<ProcessActionUseCase.Input, Unit> {

    data class Input(
        val route: NavigationRoute,
        val action: ActionModelDomain,
        val jsonElement: JsonElement? = null,
    )

    override suspend fun invoke(input: Input) {
        coroutineScopes.event.await {
            with(input) {
                handlers
                    .asSequence()
                    .filter { it.accept(route, action, jsonElement) }
                    .sortedBy { it.priority }
                    .let {
                        for (handler in it) {
                            handler.process(route, action, jsonElement)
                        }
                    }
            }
        }
    }
}