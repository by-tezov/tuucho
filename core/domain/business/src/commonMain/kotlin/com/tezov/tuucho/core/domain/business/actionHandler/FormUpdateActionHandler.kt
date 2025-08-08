package com.tezov.tuucho.core.domain.business.actionHandler

import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.business.model.ActionModelDomain
import com.tezov.tuucho.core.domain.business.model.schema._system.withScope
import com.tezov.tuucho.core.domain.business.model.schema.material.IdSchema
import com.tezov.tuucho.core.domain.business.model.schema.material.TypeSchema
import com.tezov.tuucho.core.domain.business.model.schema.material._element.form.FormSchema
import com.tezov.tuucho.core.domain.business.model.schema.material.content.action.Action
import com.tezov.tuucho.core.domain.business.model.schema.response.FormSendResponseSchema
import com.tezov.tuucho.core.domain.business.protocol.ActionHandlerProtocol
import com.tezov.tuucho.core.domain.business.protocol.SourceIdentifierProtocol
import com.tezov.tuucho.core.domain.tool.async.Notifier
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import org.koin.core.component.KoinComponent

class FormUpdateActionHandler : ActionHandlerProtocol, KoinComponent {

    data class Event(
        val source: SourceIdentifierProtocol,
        val jsonObject: JsonObject,
    )

    private val _events = Notifier.Emitter<Event>()
    val events get() = _events.createCollector

    override val priority: Int
        get() = ActionHandlerProtocol.Priority.DEFAULT

    override fun accept(
        source: SourceIdentifierProtocol,
        action: ActionModelDomain,
        jsonElement: JsonElement?,
    ): Boolean {
        return action.command == Action.Form.command && action.authority == Action.Form.Update.authority
    }

    override suspend fun process(
        source: SourceIdentifierProtocol,
        action: ActionModelDomain,
        jsonElement: JsonElement?,
    ) {
        when (val target = action.target) {
            Action.Form.Update.Target.error -> updateErrorState(source, jsonElement)
            else -> throw DomainException.Default("Unknown target $target")
        }
    }

    private suspend fun updateErrorState(
        source: SourceIdentifierProtocol,
        jsonElement: JsonElement?,
    ) {
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
                    source = source,
                    jsonObject = message
                )
            )
        }
    }

}