package com.tezov.tuucho.core.domain.actionHandler

import com.tezov.tuucho.core.domain._system.booleanOrNull
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

    override fun accept(id: String, action: String, params: Map<String, String>?): Boolean {
        return action.command() == "send-form" && action.authority() == "url"
    }

    override suspend fun process(
        id: String,
        action: String,
        params: Map<String, String>?
    ): Boolean {
        val form = materialState.form()
        if (form.isAllValid()) {
            val response = sendData.invoke(action.target(), form.data())
            when {
                response != null && response["isSuccess"].booleanOrNull == true -> {
                    params?.actionValidated(id)
                }

                response != null && response["error-reasons"] is JsonObject -> {
                    actionDenied(id, response["error-reasons"]!!.jsonObject)
                }
            }
        }
        actionDenied(id, null)
        return true
    }

    private fun Map<String, String>.actionValidated(id: String) = this["action-validated"]
        ?.let { actionHandler.invoke(id, it) }

    private fun actionDenied(
        id: String,
        reasons: Map<String, JsonElement>?
    ) {
        //TODO: reasons passed + do the update-form handler
        actionHandler.invoke(id, "update-form://error", null)
    }

}