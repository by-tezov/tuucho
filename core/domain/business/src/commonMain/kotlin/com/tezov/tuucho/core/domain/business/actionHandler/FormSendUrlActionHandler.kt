package com.tezov.tuucho.core.domain.business.actionHandler

import com.tezov.tuucho.core.domain.business.model.Action
import com.tezov.tuucho.core.domain.business.model.ActionModelDomain
import com.tezov.tuucho.core.domain.business.model.schema._system.SchemaScope
import com.tezov.tuucho.core.domain.business.model.schema._system.withScope
import com.tezov.tuucho.core.domain.business.model.schema.material.IdSchema
import com.tezov.tuucho.core.domain.business.model.schema.response.FormSendResponseSchema
import com.tezov.tuucho.core.domain.business.protocol.ActionHandlerProtocol
import com.tezov.tuucho.core.domain.business.protocol.state.ScreenStateProtocol
import com.tezov.tuucho.core.domain.business.protocol.state.form.FormsStateProtocol
import com.tezov.tuucho.core.domain.business.usecase.ActionHandlerUseCase
import com.tezov.tuucho.core.domain.business.usecase.SendDataUseCase
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class FormSendUrlActionHandler(
    private val materialState: ScreenStateProtocol,
    private val sendData: SendDataUseCase,
) : ActionHandlerProtocol, KoinComponent {

    private val actionHandler: ActionHandlerUseCase by inject()

    override val priority: Int
        get() = ActionHandlerProtocol.Priority.DEFAULT

    override fun accept(id: String, action: ActionModelDomain, jsonElement: JsonElement?): Boolean {
        return action.command == Action.Form.Send.command && action.authority == Action.Form.Send.Authority.url
    }

    override suspend fun process(
        url: String,
        id: String,
        action: ActionModelDomain,
        jsonElement: JsonElement?,
    ) {
        action.target ?: return
        val form = materialState.form().also { it.updateAllValidity() }
        if (form.isAllValid()) {
            val response = sendData.invoke(action.target, form.data())
            response?.let {
                val rootScope = response.withScope(FormSendResponseSchema.Root::Scope)
                val isAllSuccess = rootScope.isAllSuccess == true
                if (isAllSuccess) {
                    jsonElement?.actionValidated(url, id)
                } else {
                    rootScope.processInvalidRemoteForm(url, id)
                }
            }
        } else {
            form.processInvalidLocalForm(url, id)
        }
        actionDenied(url, id, null)
    }

    private fun FormsStateProtocol.processInvalidLocalForm(url: String, id: String) {
        val results = getAllValidityResult().filter { !it.second }.map {
            JsonNull.withScope(IdSchema::Scope).apply {
                self = JsonNull.withScope(IdSchema::Scope)
                    .apply { value = it.first }.collect()
            }.collect()
        }.let(::JsonArray)
        actionDenied(url, id, results)
    }

    private fun FormSendResponseSchema.Root.Scope.processInvalidRemoteForm(
        url: String,
        id: String,
    ) {
        val results = results?.map { result ->
            val resultScope = result.withScope(FormSendResponseSchema.Result::Scope)
            val resultFailureReason = resultScope.failureReason
            JsonNull.withScope(::SchemaScope).apply {
                withScope(IdSchema::Scope).apply {
                    self = JsonNull.withScope(IdSchema::Scope).apply {
                        value = resultScope.id
                    }.collect()
                }
                resultFailureReason?.let {
                    withScope(FormSendResponseSchema.Result::Scope).apply {
                        failureReason = it
                    }
                }
            }.collect()
        }?.let(::JsonArray)
        actionDenied(url, id, results)
    }

    private fun JsonElement.actionValidated(url: String, id: String) {
        val actionValidated = withScope(FormSendResponseSchema.ActionParams::Scope).actionValidated
        actionValidated?.let { actionHandler.invoke(url, id, ActionModelDomain.from(it)) }
    }

    private fun actionDenied(
        url: String,
        id: String,
        results: JsonElement?,
    ) {
        actionHandler.invoke(
            url = url,
            id = id,
            action = ActionModelDomain.from(
                command = Action.Form.Update.command,
                authority = Action.Form.Update.Authority.error,
                target = null
            ),
            paramElement = results
        )
    }

}