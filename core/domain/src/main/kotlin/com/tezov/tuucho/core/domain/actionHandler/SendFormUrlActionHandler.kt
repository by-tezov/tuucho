package com.tezov.tuucho.core.domain.actionHandler

import com.tezov.tuucho.core.domain._system.booleanOrNull
import com.tezov.tuucho.core.domain._system.string
import com.tezov.tuucho.core.domain.protocol.ActionHandlerProtocol
import com.tezov.tuucho.core.domain.protocol.state.MaterialStateProtocol
import com.tezov.tuucho.core.domain.usecase.ActionHandlerUseCase
import com.tezov.tuucho.core.domain.usecase.SendDataUseCase
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SendFormUrlActionHandler(
    private val materialState: MaterialStateProtocol,
    private val sendData: SendDataUseCase,
) : ActionHandlerProtocol, KoinComponent {

    private val actionHandler: ActionHandlerUseCase by inject()

    override val priority: Int
        get() = ActionHandlerProtocol.Priority.DEFAULT

    override fun accept(id: String?, action: String, params: JsonElement?): Boolean {
        return action.command() == "send-form" && action.authority() == "url"
    }

    override suspend fun process(
        id: String?,
        action: String,
        params: JsonElement?
    ): Boolean {
        val form = materialState.form()
        if (form.isAllValid()) {
            val response = sendData.invoke(action.target(), form.data())
            when {
                response != null && response.jsonObject["isSuccess"].booleanOrNull == true -> {
                    params?.actionValidated(id)
                }

                response != null && response.jsonObject["error-reasons"] is JsonObject -> {
                    actionDenied(id, response.jsonObject["error-reasons"]!!.jsonObject)
                }
            }
        }
        actionDenied(id, null)
        return true
    }

    private fun JsonElement.actionValidated(id: String?) = this.jsonObject["action-validated"]
        ?.let { actionHandler.invoke(id, it.string) }

    private fun actionDenied(
        id: String?,
        reasons: Map<String, JsonElement>?
    ) {
        //TODO: reasons passed + do the update-form handler
        actionHandler.invoke(id, "update-form://error", null)
    }

}