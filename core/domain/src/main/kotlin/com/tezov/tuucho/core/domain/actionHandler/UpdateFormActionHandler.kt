package com.tezov.tuucho.core.domain.actionHandler

import com.tezov.tuucho.core.domain.protocol.ActionHandlerProtocol
import com.tezov.tuucho.core.domain.protocol.state.MaterialStateProtocol
import kotlinx.serialization.json.JsonElement
import org.koin.core.component.KoinComponent

class UpdateFormActionHandler(
    private val materialState: MaterialStateProtocol,
) : ActionHandlerProtocol, KoinComponent {

    override val priority: Int
        get() = ActionHandlerProtocol.Priority.DEFAULT

    override fun accept(id: String, action: String, params: JsonElement?): Boolean {
        return action.command() == "update-form"
    }

    override suspend fun process(
        id: String,
        action: String,
        params: JsonElement?
    ): Boolean {
        //TODO
        return false
    }

}