package com.tezov.tuucho.core.ui.renderer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.tezov.tuucho.core.domain._system.stringOrNull
import com.tezov.tuucho.core.domain.actionHandler.FormUpdateActionHandler
import com.tezov.tuucho.core.domain.config.Language
import com.tezov.tuucho.core.domain.model.schema._system.onScope
import com.tezov.tuucho.core.domain.model.schema._system.withScope

import com.tezov.tuucho.core.domain.model.schema.material.IdSchema
import com.tezov.tuucho.core.domain.model.schema.material.SubsetSchema
import com.tezov.tuucho.core.domain.model.schema.material.TextSchema
import com.tezov.tuucho.core.domain.model.schema.material.TypeSchema
import com.tezov.tuucho.core.domain.model.schema.material.ValidatorSchema
import com.tezov.tuucho.core.domain.model.schema.material._element.FieldSchema
import com.tezov.tuucho.core.domain.protocol.FieldValidatorProtocol
import com.tezov.tuucho.core.domain.protocol.state.MaterialStateProtocol
import com.tezov.tuucho.core.domain.usecase.GetLanguageUseCase
import com.tezov.tuucho.core.domain.usecase.RegisterUpdateFormEventUseCase
import com.tezov.tuucho.core.domain.usecase.ValidatorFactoryUseCase
import com.tezov.tuucho.core.ui.exception.UiException
import com.tezov.tuucho.core.ui.renderer._system.ComposableScreenProtocol
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject

class FieldRendered(
    private val materialState: MaterialStateProtocol,
    private val validatorFactory: ValidatorFactoryUseCase,
    private val registerUpdateFormEventUseCase: RegisterUpdateFormEventUseCase,
    private val getLanguage: GetLanguageUseCase,
) : Renderer() {

    override fun accept(element: JsonElement) = element
        .let {
            it.withScope(TypeSchema::Scope).self == TypeSchema.Value.component &&
                    it.withScope(SubsetSchema::Scope).self == FieldSchema.Component.Value.subset
        }

    override fun process(element: JsonElement): ComposableScreenProtocol {
        val id = element.onScope(IdSchema::Scope).value ?: throw UiException.Default("Missing id for $element")
        val content = element.onScope(FieldSchema.Content::Scope)
        val option = element.onScope(FieldSchema.Option::Scope)

        val validators = buildValidator(option.validator, content.messageError)

        materialState.form().fieldsState()
            .addField(id, "", validators)

        val showError = mutableStateOf(false)
        val messageErrorExtra = mutableStateOf<String?>(null)
        registerUpdateFormEventUseCase.invoke { event ->
            if (event !is FormUpdateActionHandler.Event.Error) return@invoke
            event.takeIf { it.id == id }.let {
                val isValid = materialState.form().fieldsState().isValid(id)
                val messageError = event.text?.withScope(TextSchema::Scope)?.default
                val shouldShowError = isValid != true || messageError != null
                if (shouldShowError) {
                    showError.value = true
                    messageErrorExtra.value = messageError
                }
            }
        }

        return FieldScreen(
            title = content.title,
            placeholder = content.placeholder,
            validators = validators,
            showError = showError,
            messageErrorExtra = messageErrorExtra,
            language = getLanguage.invoke(),
            onValueChanged = { _, newValue ->
                materialState
                    .form()
                    .fieldsState()
                    .updateField(id, newValue)
                newValue
            }
        )
    }

    private fun buildValidator(
        validators: JsonArray?,
        messagesError: JsonArray?,
    ): List<FieldValidatorProtocol<String>>? {
        val messagesErrorIdMapped = messagesError?.mapNotNull { message ->
            val idMessage = message.onScope(IdSchema::Scope).value ?: throw UiException.Default("Missing id for $message")
            idMessage to message.jsonObject
        }

        return validators?.mapNotNull { validator ->
            val validatorPrototype = validator.withScope(ValidatorSchema::Scope).apply {
                this.messageError = messagesErrorIdMapped
                    ?.firstOrNull { it.first == idMessageError || validators.size == 1 }?.second
                    ?: throw UiException.Default("Missing message error for validator")
            }.collect()

            @Suppress("UNCHECKED_CAST")
            validatorFactory.invoke(validatorPrototype) as? FieldValidatorProtocol<String>
        }
    }

}

class FieldScreen(
    var title: JsonObject?,
    var placeholder: JsonObject?,
    var validators: List<FieldValidatorProtocol<String>>?,
    var showError: MutableState<Boolean>,
    var messageErrorExtra: MutableState<String?>,
    var language: Language,
    var onValueChanged: (previousValue: String, newValue: String) -> String?,
) : ComposableScreenProtocol() {

    @Composable
    override fun show(scope: Any?) {
        //TODO validators:
        //  - when lost focus, if not empty test validator and update the error status
        //  - when gain focus and user write, remove the error while user is typing

        val text = remember { mutableStateOf("") }
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = text.value,
            onValueChange = {
                onValueChanged(text.value, it)?.let { acceptedValue ->
                    showError.value = false
                    text.value = acceptedValue
                }
            },
            label = {
                Text(title?.get(language.code)?.stringOrNull ?: "")
            },
            placeholder = {
                Text(placeholder?.get(language.code)?.stringOrNull ?: "")
            },
            singleLine = true,
            isError = showError.value,
            supportingText = {
                if (showError.value) {
                    Column {
                        validators?.filter { !it.isValid() }?.forEach {
                            Text(
                                it.getErrorMessage(language),
                                fontSize = 13.sp //TODO
                            )
                        }
                        messageErrorExtra.value?.let {
                            Text(
                                it,
                                fontSize = 13.sp //TODO
                            )
                        }
                    }
                }
            }
        )
    }
}