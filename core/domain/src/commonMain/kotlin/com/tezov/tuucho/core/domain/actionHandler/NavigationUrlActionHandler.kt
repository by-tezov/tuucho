package com.tezov.tuucho.core.domain.actionHandler

import com.tezov.tuucho.core.domain.model.Action
import com.tezov.tuucho.core.domain.model.ActionModelDomain
import com.tezov.tuucho.core.domain.protocol.ActionHandlerProtocol
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonElement

class NavigationUrlActionHandler : ActionHandlerProtocol {
    private val _events = MutableSharedFlow<String>(replay = 0)
    val events: SharedFlow<String> = _events

    override val priority: Int
        get() = ActionHandlerProtocol.Priority.DEFAULT

    override fun accept(id: String?, action: ActionModelDomain, params: JsonElement?): Boolean {
        return action.command == Action.Navigate.command && action.authority == Action.Navigate.Authority.url
    }

    override suspend fun process(id: String?, action: ActionModelDomain, params: JsonElement?) {
        action.target?.let {
            withContext(Dispatchers.Main) {
                _events.emit(it)
            }
        }
    }

}