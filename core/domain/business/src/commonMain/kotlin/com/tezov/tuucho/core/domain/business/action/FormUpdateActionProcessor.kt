package com.tezov.tuucho.core.domain.business.action

import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.IdSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material._element.form.FormSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.content.action.Action
import com.tezov.tuucho.core.domain.business.jsonSchema.response.FormSendResponseSchema
import com.tezov.tuucho.core.domain.business.model.ActionModelDomain
import com.tezov.tuucho.core.domain.business.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.protocol.ActionProcessorProtocol
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.tool.async.Notifier
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import org.koin.core.component.KoinComponent

class FormUpdateActionProcessor(
    private val coroutineScopes: CoroutineScopesProtocol,
) : ActionProcessorProtocol, KoinComponent {

    data class Event(
        val route: NavigationRoute,
        val jsonObject: JsonObject,
    )

    private val _events = Notifier.Emitter<Event>()
    val events get() = _events.createCollector

    override val priority: Int
        get() = ActionProcessorProtocol.Priority.DEFAULT

    override fun accept(
        route: NavigationRoute,
        action: ActionModelDomain,
        jsonElement: JsonElement?,
    ): Boolean {
        return action.command == Action.Form.command && action.authority == Action.Form.Update.authority
    }

    override suspend fun process(
        route: NavigationRoute,
        action: ActionModelDomain,
        jsonElement: JsonElement?,
    ) {
        when (val target = action.target) {
            Action.Form.Update.Target.error -> updateErrorState(route, jsonElement)
            else -> throw DomainException.Default("Unknown target $target")
        }
    }

    private suspend fun updateErrorState(
        route: NavigationRoute,
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
            coroutineScopes.event.async {
                _events.emit(
                    Event(
                        route = route,
                        jsonObject = message
                    )
                )
            }
        }
    }

}