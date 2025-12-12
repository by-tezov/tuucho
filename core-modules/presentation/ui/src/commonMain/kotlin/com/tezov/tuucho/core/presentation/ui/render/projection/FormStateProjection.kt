package com.tezov.tuucho.core.presentation.ui.render.projection

import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TextSchema
import com.tezov.tuucho.core.domain.business.protocol.screen.view.FormStateProtocol
import com.tezov.tuucho.core.presentation.ui._system.idValue
import com.tezov.tuucho.core.presentation.ui.exception.UiException
import com.tezov.tuucho.core.presentation.ui.render.protocol.ProjectionProtocol
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

class FormStateProjection(
    override val key: String,
) : ProjectionProtocol, FormStateProtocol {

    lateinit var componentId: String
    lateinit var validatorProjection: FormValidatorProjection
    lateinit var fieldValueProjection: TextProjection.Mutable

    private lateinit var messagesErrorIdMapped: Map<String, JsonObject>

    var supportingTexts: List<String>? = null

    override fun updateValidity() {
        validatorProjection.updateValidity(getValue())
        updateSupportingText()
    }

    override fun isValid() = validatorProjection.isValid()

    override fun getId() = componentId

    override fun getValue() = fieldValueProjection.value

    override var isReady = false
        private set

    override suspend fun process(jsonElement: JsonElement?) {
        messagesErrorIdMapped = buildMap {
            (jsonElement as? JsonArray)?.mapNotNull { message ->
                if (message !is JsonObject) return@mapNotNull null
                val idMessage = message.idValue
                this[idMessage] = message
            }
        }
        isReady = true
    }

    private fun updateSupportingText() {
        supportingTexts = buildList {
            validatorProjection.validators?.forEach { validator ->
                if (validator.isValid) {
                    validator.errorMessagesId?.let {
                        val messageObject = messagesErrorIdMapped[validator.errorMessagesId]
                            ?: throw UiException.Default("Missing validator message for id ${validator.errorMessagesId}")
                        messageObject.withScope(TextSchema::Scope).default?.let {
                            add(it)
                        }
                    } ?: run {
                        val messageObject = messagesErrorIdMapped.values.firstOrNull()
                            ?: throw UiException.Default("Missing validator message")
                        messageObject.withScope(TextSchema::Scope).default?.let {
                            add(it)
                        }
                    }
                }
            }
        }
    }
}
