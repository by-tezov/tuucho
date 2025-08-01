package com.tezov.tuucho.core.domain.usecase

import com.tezov.tuucho.core.domain.model.ActionModelDomain
import com.tezov.tuucho.core.domain.protocol.ActionHandlerProtocol
import com.tezov.tuucho.core.domain.protocol.CoroutineScopesProtocol
import kotlinx.serialization.json.JsonElement

class ActionHandlerUseCase(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val handlers: List<ActionHandlerProtocol>,
) {
    fun invoke(url: String, id: String, action: ActionModelDomain, paramElement: JsonElement? = null) {
        coroutineScopes.launchOnEvent {
            handlers
                .asSequence()
                .filter { it.accept(id, action, paramElement) }
                .sortedBy { it.priority }
                .let {
                    for (handler in it) {
                        handler.process(url, id, action, paramElement)
                    }
                }
        }
    }
}