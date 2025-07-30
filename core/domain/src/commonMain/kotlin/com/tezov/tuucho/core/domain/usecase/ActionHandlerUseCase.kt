package com.tezov.tuucho.core.domain.usecase

import com.tezov.tuucho.core.domain.model.ActionModelDomain
import com.tezov.tuucho.core.domain.protocol.ActionHandlerProtocol
import com.tezov.tuucho.core.domain.protocol.CoroutineScopeProviderProtocol
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonElement

class ActionHandlerUseCase(
    private val coroutineScopeProvider: CoroutineScopeProviderProtocol,
    private val handlers: List<ActionHandlerProtocol>,
) {
    fun invoke(url: String, id: String, action: ActionModelDomain, params: JsonElement? = null) {
        coroutineScopeProvider.event.launch {
            handlers
                .asSequence()
                .filter { it.accept(id, action, params) }
                .sortedBy { it.priority }
                .let {
                    for (handler in it) {
                        handler.process(url, id, action, params)
                    }
                }
        }
    }
}