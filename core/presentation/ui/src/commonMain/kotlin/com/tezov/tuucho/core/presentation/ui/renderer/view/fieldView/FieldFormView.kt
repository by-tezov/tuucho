package com.tezov.tuucho.core.presentation.ui.renderer.view.fieldView

import com.tezov.tuucho.core.domain.business.model.schema._system.withScope
import com.tezov.tuucho.core.domain.business.model.schema.material.IdSchema.idValue
import com.tezov.tuucho.core.domain.business.model.schema.material.ValidatorSchema
import com.tezov.tuucho.core.domain.business.protocol.screen.FieldValidatorProtocol
import com.tezov.tuucho.core.domain.business.protocol.screen.view.ViewProtocol
import com.tezov.tuucho.core.domain.business.protocol.screen.view.form.FieldFormViewProtocol
import com.tezov.tuucho.core.domain.business.usecase.ValidatorFactoryUseCase
import com.tezov.tuucho.core.domain.business.usecase._system.UseCaseExecutor
import com.tezov.tuucho.core.presentation.ui.exception.UiException
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject

class FieldFormView(
    private val useCaseExecutor: UseCaseExecutor,
    private val validatorFactory: ValidatorFactoryUseCase,
) : FieldFormViewProtocol {

    private lateinit var fieldView: FieldView

    override var validators: List<FieldValidatorProtocol<String>>? = null
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
        return validators?.all { it.isValid() }
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
            val validatorPrototype = validatorObject.withScope(ValidatorSchema::Scope).apply {
                this.messageError = messagesErrorIdMapped
                    .firstOrNull { it.first == idMessageError || validatorsArray.size == 1 }?.second
                    ?: throw UiException.Default("Missing message error for validator")
            }.collect()
            @Suppress("UNCHECKED_CAST")
            useCaseExecutor.invokeSuspend(
                useCase = validatorFactory,
                input = ValidatorFactoryUseCase.Input(
                    prototypeObject = validatorPrototype
                ),
            ).validator as? FieldValidatorProtocol<String>
        }?.takeIf { it.isNotEmpty() }
    }

}