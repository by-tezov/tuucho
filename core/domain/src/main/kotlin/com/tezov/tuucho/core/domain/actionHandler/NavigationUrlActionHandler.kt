package com.tezov.tuucho.core.domain.actionHandler

import com.tezov.tuucho.core.domain.protocol.ActionHandlerProtocol
import com.tezov.tuucho.core.domain.protocol.CoroutineDispatchersProtocol
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class NavigationUrlActionHandler(
    coroutineDispatchers: CoroutineDispatchersProtocol
) : ActionHandlerProtocol {
    private val coroutineScope = CoroutineScope(coroutineDispatchers.main)
    private val _events = MutableSharedFlow<String>(replay = 0)
    val events: SharedFlow<String> = _events

    override val priority: Int
        get() = ActionHandlerProtocol.Priority.DEFAULT

    override fun accept(id: String, action: String): Boolean {
        return action.action() == "navigate" && action.authority() == "url"
    }

    override fun process(id: String, action: String): Boolean {
        coroutineScope.launch { _events.emit(action.target()) }
        return true
    }

}