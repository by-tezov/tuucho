package com.tezov.tuucho.core.domain.business.actionHandler

import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.business.model.Action
import com.tezov.tuucho.core.domain.business.model.ActionModelDomain
import com.tezov.tuucho.core.domain.business.model.schema._system.withScope
import com.tezov.tuucho.core.domain.business.model.schema.material.IdSchema
import com.tezov.tuucho.core.domain.business.model.schema.material.TypeSchema
import com.tezov.tuucho.core.domain.business.model.schema.material._element.form.FormSchema
import com.tezov.tuucho.core.domain.business.model.schema.response.FormSendResponseSchema
import com.tezov.tuucho.core.domain.business.protocol.ActionHandlerProtocol
import com.tezov.tuucho.core.domain.tool.async.Notifier
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import org.koin.core.component.KoinComponent

class FormUpdateActionHandler : ActionHandlerProtocol, KoinComponent {

    data class Event(
        val url: String,
        val jsonObject: JsonObject,
    )

    private val _events = Notifier.Emitter<Event>()
    val events get() = _events.createCollector

    override val priority: Int
        get() = ActionHandlerProtocol.Priority.DEFAULT

    override fun accept(id: String, action: ActionModelDomain, jsonElement: JsonElement?): Boolean {
        return action.command == Action.Form.Update.command
    }

    override suspend fun process(
        url: String,
        id: String,
        action: ActionModelDomain,
        jsonElement: JsonElement?,
    ) {
        when (val authority = action.authority) {
            Action.Form.Update.Authority.error -> updateErrorState(url, jsonElement)
            else -> throw DomainException.Default("Unknown authority $authority")
        }
    }

    private suspend fun updateErrorState(url: String, jsonElement: JsonElement?) {
        jsonElement?.jsonArray?.forEach { param ->
            val message = JsonNull.withScope(FormSchema.Message::Scope).apply {
                id = param.withScope(IdSchema::Scope).self
                type = TypeSchema.Value.message
                subset = FormSchema.Message.Value.Subset.updateErrorState
                param.withScope(FormSendResponseSchema.Result::Scope).failureReason?.let {
                    messageErrorExtra = it
                }
            }.collect()
            _events.emit(
                Event(
                    url = url,
                    jsonObject = message
                )
            )
        }
    }

}