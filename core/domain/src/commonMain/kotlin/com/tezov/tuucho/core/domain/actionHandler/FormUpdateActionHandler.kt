package com.tezov.tuucho.core.domain.actionHandler

import com.tezov.tuucho.core.domain.model.Action
import com.tezov.tuucho.core.domain.model.ActionModelDomain

import com.tezov.tuucho.core.domain.model.schema._system.SchemaScope
import com.tezov.tuucho.core.domain.model.schema._system.withScope
import com.tezov.tuucho.core.domain.model.schema.material.IdSchema
import com.tezov.tuucho.core.domain.model.schema.response.FormSendResponseSchema
import com.tezov.tuucho.core.domain.protocol.ActionHandlerProtocol
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonArray
import org.koin.core.component.KoinComponent

class FormUpdateActionHandler : ActionHandlerProtocol, KoinComponent {

    sealed class Event {
        class Error(
            val id: String,
            val text: JsonElement?,
        ) : Event()
    }

    private val _events = MutableSharedFlow<Event>(replay = 0)
    val events: SharedFlow<Event> = _events

    override val priority: Int
        get() = ActionHandlerProtocol.Priority.DEFAULT

    override fun accept(id: String?, action: ActionModelDomain, params: JsonElement?): Boolean {
        return action.command == Action.Form.Update.command
    }

    override suspend fun process(
        id: String?,
        action: ActionModelDomain,
        params: JsonElement?,
    ) {
        when (val authority = action.authority) {
            Action.Form.Update.Authority.error -> updateErrorState(params)
            else -> error("unknown authority $authority")
        }
    }

    private suspend fun updateErrorState(params: JsonElement?) {
        withContext(Dispatchers.Main) {
            params?.jsonArray?.forEach {
                val scope = it.withScope(::SchemaScope)
                val event = Event.Error(
                    id = scope.onScope(IdSchema::Scope).value ?: error("Missing id for $it"),
                    text = scope.withScope(FormSendResponseSchema.Result::Scope).failureReason
                )
                _events.emit(event)
            }
        }
    }

}