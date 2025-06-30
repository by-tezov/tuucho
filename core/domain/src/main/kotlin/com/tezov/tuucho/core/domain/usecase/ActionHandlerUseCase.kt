package com.tezov.tuucho.core.domain.usecase

import com.tezov.tuucho.core.domain.protocol.ActionHandlerProtocol
import com.tezov.tuucho.core.domain.protocol.CoroutineDispatchersProtocol
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class ActionHandlerUseCase(
    coroutineDispatchers: CoroutineDispatchersProtocol,
    private val handlers: List<ActionHandlerProtocol>
) {
    private val coroutineScope = CoroutineScope(coroutineDispatchers.default)

    fun invoke(id: String, action: String, params: Map<String, String>? = null) {
        handlers
            .asSequence()
            .filter { it.accept(id, action, params) }
            .sortedBy { it.priority }
            .let {
                coroutineScope.launch {
                    for(handler in it) {
                        if(handler.process(id, action, params)) {
                            break
                        }
                    }
                }
            }
    }
}