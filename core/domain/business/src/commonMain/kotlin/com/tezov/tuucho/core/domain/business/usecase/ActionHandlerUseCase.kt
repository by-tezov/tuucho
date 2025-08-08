package com.tezov.tuucho.core.domain.business.usecase

import com.tezov.tuucho.core.domain.business.model.ActionModelDomain
import com.tezov.tuucho.core.domain.business.protocol.ActionHandlerProtocol
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.SourceIdentifierProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import kotlinx.serialization.json.JsonElement

class ActionHandlerUseCase(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val handlers: List<ActionHandlerProtocol>,
) : UseCaseProtocol.Async<ActionHandlerUseCase.Input, Unit> {

    data class Input(
        val source: SourceIdentifierProtocol,
        val action: ActionModelDomain,
        val jsonElement: JsonElement? = null,
    )

    override suspend fun invoke(input: Input) = with(input) {
        coroutineScopes.onEvent {
            handlers
                .asSequence()
                .filter { it.accept(source, action, jsonElement) }
                .sortedBy { it.priority }
                .let {
                    for (handler in it) {
                        handler.process(source, action, jsonElement)
                    }
                }
        }
    }
}