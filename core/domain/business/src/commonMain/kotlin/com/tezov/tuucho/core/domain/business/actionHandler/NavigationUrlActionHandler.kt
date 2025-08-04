package com.tezov.tuucho.core.domain.business.actionHandler

import com.tezov.tuucho.core.domain.business.model.Action
import com.tezov.tuucho.core.domain.business.model.ActionModelDomain
import com.tezov.tuucho.core.domain.business.protocol.ActionHandlerProtocol
import com.tezov.tuucho.core.domain.tool.async.Notifier
import kotlinx.serialization.json.JsonElement

class NavigationUrlActionHandler() : ActionHandlerProtocol {
    private val _events = Notifier.Emitter<String>()
    val events get() = _events.createCollector

    override val priority: Int
        get() = ActionHandlerProtocol.Priority.DEFAULT

    override fun accept(id: String, action: ActionModelDomain, jsonElement: JsonElement?): Boolean {
        return action.command == Action.Navigate.command && action.authority == Action.Navigate.Authority.url
    }

    override suspend fun process(
        url: String,
        id: String,
        action: ActionModelDomain,
        jsonElement: JsonElement?,
    ) {
        action.target?.let { _events.emit(it) }
    }

}