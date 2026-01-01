package com.tezov.tuucho.core.presentation.ui.render.projection.form

import com.tezov.tuucho.core.domain.business.protocol.screen.view.FormStateProtocol
import com.tezov.tuucho.core.presentation.ui.exception.UiException
import com.tezov.tuucho.core.presentation.ui.render.projection.TextProjectionProtocol
import com.tezov.tuucho.core.presentation.ui.render.projection.TextsProjectionProtocol

interface FormStateProjectionProtocol : FormStateProtocol {
    val supportingTexts: List<String>?

    var messageLazyId: Lazy<String?>
    var messageErrorProjection: TextsProjectionProtocol
    var validatorProjection: FormValidatorProjectionProtocol
    var fieldValueProjection: TextProjectionProtocol
}

private class FormStateProjection : FormStateProjectionProtocol {
    override lateinit var messageLazyId: Lazy<String?>

    override lateinit var messageErrorProjection: TextsProjectionProtocol
    override lateinit var validatorProjection: FormValidatorProjectionProtocol
    override lateinit var fieldValueProjection: TextProjectionProtocol

    override var supportingTexts: List<String>? = null

    override fun updateValidity() {
        validatorProjection.updateValidity(getValue())
        updateSupportingText()
    }

    override fun isValid() = validatorProjection.isValid()

    override fun getId() = messageLazyId.value ?: throw UiException.Default("should not be possible")

    override fun getValue() = fieldValueProjection.value

    private fun updateSupportingText() {
        supportingTexts = buildList {
            validatorProjection.validators.forEach { validator ->
                if (!validator.isValid) {
                    val messageObject = validator.errorMessagesId?.let {
                        messageErrorProjection.texts
                            .firstOrNull { it.id == validator.errorMessagesId }
                            ?: throw UiException.Default("Missing validator message for id ${validator.errorMessagesId}")
                    } ?: run {
                        messageErrorProjection.texts
                            .firstOrNull()
                            ?: throw UiException.Default("Missing validator message")
                    }
                    messageObject.value?.let { add(it) }
                }
            }
        }
    }
}

fun createFormStateProjection(): FormStateProjectionProtocol = FormStateProjection()

fun field(): FormStateProjectionProtocol = createFormStateProjection()
