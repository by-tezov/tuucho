package com.tezov.tuucho.core.domain.actionHandler

import com.tezov.tuucho.core.domain.model.Action
import com.tezov.tuucho.core.domain.model.ActionModelDomain
import com.tezov.tuucho.core.domain.model.schema._system.Schema.Companion.schema
import com.tezov.tuucho.core.domain.model.schema._system.SchemaScope
import com.tezov.tuucho.core.domain.model.schema.material.IdSchema
import com.tezov.tuucho.core.domain.model.schema.response.FormSendResponseSchema
import com.tezov.tuucho.core.domain.protocol.ActionHandlerProtocol
import com.tezov.tuucho.core.domain.protocol.state.FormMaterialStateProtocol
import com.tezov.tuucho.core.domain.protocol.state.MaterialStateProtocol
import com.tezov.tuucho.core.domain.usecase.ActionHandlerUseCase
import com.tezov.tuucho.core.domain.usecase.SendDataUseCase
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class FormSendUrlActionHandler(
    private val materialState: MaterialStateProtocol,
    private val sendData: SendDataUseCase,
) : ActionHandlerProtocol, KoinComponent {

    private val actionHandler: ActionHandlerUseCase by inject()

    override val priority: Int
        get() = ActionHandlerProtocol.Priority.DEFAULT

    override fun accept(id: String?, action: ActionModelDomain, params: JsonElement?): Boolean {
        return action.command == Action.Form.Send.command && action.authority == Action.Form.Send.Authority.url
    }

    override suspend fun process(
        id: String?,
        action: ActionModelDomain,
        params: JsonElement?,
    ) {
        action.target ?: return
        val form = materialState.form().also { it.updateAllValidity() }

        if (form.isAllValid()) {
            sendData.invoke(action.target, form.data())?.schema()?.let { responseSchema ->
                val rootScope = responseSchema.withScope(FormSendResponseSchema.Root::Scope)
                val isAllSuccess = rootScope.isAllSuccess == true
                if (isAllSuccess) {
                    params?.actionValidated(id)
                } else {
                    rootScope.processInvalidRemoteForm(id)
                }
            }


        } else {
            form.processInvalidLocalForm(id)
        }
        actionDenied(id, null)
    }

    private fun FormMaterialStateProtocol.processInvalidLocalForm(id: String?) {
        val results = getAllValidityResult().filter { !it.second }.map {
            JsonNull.schema().withScope(IdSchema::Scope).apply {
                self = JsonPrimitive(it.first)
            }.collect()
        }.let(::JsonArray)
        actionDenied(id, results)
    }

    private fun FormSendResponseSchema.Root.Scope.processInvalidRemoteForm(id: String?) {
        val results = results?.map { result ->
            val resultScope = result.schema().withScope(FormSendResponseSchema.Result::Scope)
            val resultId = resultScope.id
            val resultFailureReason = resultScope.failureReason
            JsonNull.schema().withScope(::SchemaScope).apply {
                withScope(IdSchema::Scope).apply {
                    self = resultId
                }
                resultFailureReason?.let {
                    withScope(FormSendResponseSchema.Result::Scope).apply {
                        failureReason = it
                    }
                }
            }.collect()
        }?.let(::JsonArray)
        actionDenied(id, results)
    }

    private fun JsonElement.actionValidated(id: String?) {
        val actionValidated =
            schema().withScope(FormSendResponseSchema.ActionParams::Scope).actionValidated
        actionValidated?.let { actionHandler.invoke(id, ActionModelDomain.from(it)) }
    }

    private fun actionDenied(
        id: String?,
        results: JsonElement?,
    ) {
        actionHandler.invoke(
            id = id,
            action = ActionModelDomain.from(
                command = Action.Form.Update.command,
                authority = Action.Form.Update.Authority.error,
                target = null
            ),
            params = results
        )
    }

}