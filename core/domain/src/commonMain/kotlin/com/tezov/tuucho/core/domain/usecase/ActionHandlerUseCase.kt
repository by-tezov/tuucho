package com.tezov.tuucho.core.domain.usecase

import com.tezov.tuucho.core.domain.model.ActionModelDomain
import com.tezov.tuucho.core.domain.protocol.ActionHandlerProtocol
import com.tezov.tuucho.core.domain.protocol.CoroutineContextProviderProtocol
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonElement

class ActionHandlerUseCase(
    coroutineContextProvider: CoroutineContextProviderProtocol,
    private val handlers: List<ActionHandlerProtocol>,
) {
    private val coroutineScope = CoroutineScope(coroutineContextProvider.default)

    fun invoke(id: String?, action: ActionModelDomain, params: JsonElement? = null) {
        handlers
            .asSequence()
            .filter { it.accept(id, action, params) }
            .sortedBy { it.priority }
            .let {
                coroutineScope.launch {
                    for (handler in it) {
                        handler.process(id, action, params)
                    }
                }
            }
    }
}