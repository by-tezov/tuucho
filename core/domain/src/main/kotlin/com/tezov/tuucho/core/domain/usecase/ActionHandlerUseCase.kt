package com.tezov.tuucho.core.domain.usecase

import com.tezov.tuucho.core.domain.protocol.ActionHandlerProtocol

class ActionHandlerUseCase(
    private val handlers: List<ActionHandlerProtocol>
) {

    fun invoke(id: String, action: String) {
        handlers
            .asSequence()
            .filter { it.accept(id, action) }
            .sortedBy { it.priority }
            .let {
                for(handler in handlers) {
                    if(handler.process(id, action)) {
                        break
                    }
                }
            }
    }
}