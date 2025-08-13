package com.tezov.tuucho.core.domain.business.action

import com.tezov.tuucho.core.domain.business.jsonSchema.material.content.action.Action
import com.tezov.tuucho.core.domain.business.model.ActionModelDomain
import com.tezov.tuucho.core.domain.business.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.protocol.ActionProcessorProtocol
import com.tezov.tuucho.core.domain.tool.async.Notifier
import kotlinx.serialization.json.JsonElement

class NavigationUrlActionProcessor() : ActionProcessorProtocol {
    private val _events = Notifier.Emitter<String>()
    val events get() = _events.createCollector

    override val priority: Int
        get() = ActionProcessorProtocol.Priority.DEFAULT

    override fun accept(
        route: NavigationRoute,
        action: ActionModelDomain,
        jsonElement: JsonElement?,
    ): Boolean {
        return action.command == Action.Navigate.command && action.authority == Action.Navigate.Url.authority
    }

    override suspend fun process(
        route: NavigationRoute,
        action: ActionModelDomain,
        jsonElement: JsonElement?,
    ) {
        action.target?.let { _events.emit(it) }
    }

}