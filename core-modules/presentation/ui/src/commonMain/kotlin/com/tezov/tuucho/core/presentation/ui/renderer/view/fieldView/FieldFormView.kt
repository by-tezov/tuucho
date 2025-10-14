package com.tezov.tuucho.core.presentation.ui.renderer.view.fieldView

import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.IdSchema.idValue
import com.tezov.tuucho.core.domain.business.jsonSchema.material._element.form.FormValidatorSchema
import com.tezov.tuucho.core.domain.business.protocol.FormValidatorProtocol
import com.tezov.tuucho.core.domain.business.protocol.screen.view.ViewProtocol
import com.tezov.tuucho.core.domain.business.protocol.screen.view.form.FieldFormViewProtocol
import com.tezov.tuucho.core.domain.business.usecase._system.UseCaseExecutor
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.FormValidatorFactoryUseCase
import com.tezov.tuucho.core.presentation.ui.exception.UiException
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject

class FieldFormView(
    private val useCaseExecutor: UseCaseExecutor,
    private val fieldValidatorFactory: FormValidatorFactoryUseCase,
) : FieldFormViewProtocol {

    private lateinit var fieldView: FieldView

    override var validators: List<FormValidatorProtocol<String>>? = null
        private set

    override fun attach(view: ViewProtocol) {
        this.fieldView = view as FieldView
    }

    override fun getId(): String {
        return fieldView.componentObject.idValue
    }

    override fun getValue(): String {
        return fieldView.value
    }

    override fun updateValidity() {
        validators?.forEach { it.updateValidity(fieldView.value) }
    }

    override fun isValid(): Boolean? {
        return validators?.all { it.isValid }
    }

    suspend fun buildValidator(
        validatorsArray: JsonArray?,
        messagesError: JsonArray,
    ) {
        val messagesErrorIdMapped = messagesError.mapNotNull { message ->
            if (message !is JsonObject) return@mapNotNull null
            val idMessage = message.idValue
            idMessage to message
        }

        validators = validatorsArray?.mapNotNull { validatorObject ->
            val validatorPrototype = validatorObject.withScope(FormValidatorSchema::Scope).apply {
                this.messageError = messagesErrorIdMapped
                    .firstOrNull { it.first == idMessageError || validatorsArray.size == 1 }?.second
                    ?: throw UiException.Default("Missing message error for validator")
            }.collect()
            @Suppress("UNCHECKED_CAST")
            useCaseExecutor.invokeSuspend(
                useCase = fieldValidatorFactory,
                input = FormValidatorFactoryUseCase.Input(
                    prototypeObject = validatorPrototype
                ),
            ).validator as? FormValidatorProtocol<String>
        }?.takeIf { it.isNotEmpty() }
    }

}