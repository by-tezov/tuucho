package com.tezov.tuucho.core.ui.uiComponentFactory

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.tezov.tuucho.core.domain._system.string
import com.tezov.tuucho.core.domain.config.Language
import com.tezov.tuucho.core.domain.model.schema._system.withScope
import com.tezov.tuucho.core.domain.model.schema.material.ComponentSchema
import com.tezov.tuucho.core.domain.model.schema.material.ComponentSchema.contentOrNull
import com.tezov.tuucho.core.domain.model.schema.material.ComponentSchema.optionOrNull
import com.tezov.tuucho.core.domain.model.schema.material.IdSchema.idValue
import com.tezov.tuucho.core.domain.model.schema.material.MessageSchema
import com.tezov.tuucho.core.domain.model.schema.material.SubsetSchema
import com.tezov.tuucho.core.domain.model.schema.material.TypeSchema
import com.tezov.tuucho.core.domain.model.schema.material.ValidatorSchema
import com.tezov.tuucho.core.domain.model.schema.material._element.form.FormFieldSchema
import com.tezov.tuucho.core.domain.model.schema.material._element.form.FormSchema
import com.tezov.tuucho.core.domain.protocol.FieldValidatorProtocol
import com.tezov.tuucho.core.domain.usecase.ValidatorFactoryUseCase
import com.tezov.tuucho.core.domain.usecase.state.AddFormInMaterialStateUseCase
import com.tezov.tuucho.core.domain.usecase.state.ClearFormInMaterialStateUseCase
import com.tezov.tuucho.core.domain.usecase.state.IsFieldFormValidUseCase
import com.tezov.tuucho.core.domain.usecase.state.UpdateFieldFormUseCase
import com.tezov.tuucho.core.ui.exception.UiException
import com.tezov.tuucho.core.ui.uiComponentFactory._system.Screen
import com.tezov.tuucho.core.ui.uiComponentFactory._system.UiComponentFactory
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject

class FieldUiComponentFactory(
    private val validatorFactory: ValidatorFactoryUseCase,
    private val addFormInMaterialState: AddFormInMaterialStateUseCase,
    private val clearFormInMaterialState: ClearFormInMaterialStateUseCase,
    private val updateFieldForm: UpdateFieldFormUseCase,
    private val isFieldFormValid: IsFieldFormValidUseCase,
) : UiComponentFactory() {

    override fun accept(componentElement: JsonObject) = componentElement.let {
        it.withScope(TypeSchema::Scope).self == TypeSchema.Value.component &&
                it.withScope(SubsetSchema::Scope).self == FormFieldSchema.Component.Value.subset
    }

    override fun process(url: String, componentElement: JsonObject) = FieldScreen(
        url = url,
        componentObject = componentElement,
        validatorFactory = validatorFactory,
        addFormInMaterialState = addFormInMaterialState,
        clearFormInMaterialState = clearFormInMaterialState,
        updateFieldForm = updateFieldForm,
        isFieldFormValid = isFieldFormValid,
    ).also { it.init() }
}

class FieldScreen(
    url: String,
    componentObject: JsonObject,
    private val validatorFactory: ValidatorFactoryUseCase,
    private val addFormInMaterialState: AddFormInMaterialStateUseCase,
    private val clearFormInMaterialState: ClearFormInMaterialStateUseCase,
    private val updateFieldForm: UpdateFieldFormUseCase,
    private val isFieldFormValid: IsFieldFormValidUseCase,
) : Screen(url, componentObject) {

    private val _title = mutableStateOf<JsonObject?>(null)
    private val _placeholder = mutableStateOf<JsonObject?>(null)
    private val _value = mutableStateOf("")
    private val _showError = mutableStateOf(false)
    private val _messageErrorExtra = mutableStateOf<JsonObject?>(null)
    private var validators: List<FieldValidatorProtocol<String>>? = null

    override fun onInit() {
        componentObject.contentOrNull
            ?.withScope(FormFieldSchema.Content::Scope)?.run {
                title?.idValue?.let {
                    addTypeIdForKey(
                        type = TypeSchema.Value.text,
                        id = it,
                        key = FormFieldSchema.Content.Key.title
                    )
                }
                placeholder?.idValue?.let {
                    addTypeIdForKey(
                        type = TypeSchema.Value.text,
                        id = it,
                        key = FormFieldSchema.Content.Key.placeholder
                    )
                }
            }
        addTypeId(TypeSchema.Value.message, id = componentObject.idValue)
    }

    override fun JsonObject.processComponent() {
        if(isInitialized) {
            clearFormInMaterialState.invoke(
                url = url,
                id = componentObject.idValue
            )
        }
        withScope(ComponentSchema::Scope).run {
            content?.processContent()
            option?.processOption()
            state?.processState()
        }
        addFormInMaterialState.invoke(
            url = url,
            initialValue = "",
            id = componentObject.idValue,
            validators = this@FieldScreen.validators
        )
    }

    override fun JsonObject.processContent() {
        withScope(FormFieldSchema.Content::Scope).run {
            title?.processText(FormFieldSchema.Content.Key.title)
            placeholder?.processText(FormFieldSchema.Content.Key.placeholder)
            messageError?.let { processValidator() }
        }
    }

    override fun JsonObject.processOption() {
        componentObject.contentOrNull
            ?.withScope(FormFieldSchema.Content::Scope)
            ?.messageError?.let { processValidator() }
    }

    override fun JsonObject.processState() {
        withScope(FormSchema.State::Scope).run {
            initialValue?.get(Language.Default.code)?.string?.let {
                _value.value = it
            }
        }
    }

    override fun JsonObject.processText(key: String) {
        when (key) {
            FormFieldSchema.Content.Key.title -> _title.value = this
            FormFieldSchema.Content.Key.placeholder -> _placeholder.value = this
        }
    }

    override fun JsonObject.processMessage() {
        when (withScope(MessageSchema::Scope).subset) {
            FormSchema.Message.Value.Subset.updateErrorState -> {
                withScope(FormSchema.Message::Scope).run {
                    val isValid = isFieldFormValid.invoke(url, componentObject.idValue)
                    val messageError = messageErrorExtra
                    val shouldShowError = isValid != true || messageError != null
                    if (shouldShowError) {
                        _showError.value = true
                        _messageErrorExtra.value = messageError
                    }
                }
            }
        }
    }

    private fun processValidator() {
        componentObject.optionOrNull?.withScope(FormSchema.Option::Scope)?.run {
            validator?.let { validators ->
                val messageError = componentObject.contentOrNull
                    ?.withScope(FormFieldSchema.Content::Scope)
                    ?.messageError
                this@FieldScreen.validators = messageError?.let {
                    buildValidator(validators, messageError)
                }
            }
        }
    }

    private fun buildValidator(
        validators: JsonArray?,
        messagesError: JsonArray,
    ): List<FieldValidatorProtocol<String>>? {
        val messagesErrorIdMapped = messagesError.mapNotNull { message ->
            if (message !is JsonObject) return@mapNotNull null
            val idMessage = message.idValue
            idMessage to message
        }

        return validators?.mapNotNull { validator ->
            val validatorPrototype = validator.withScope(ValidatorSchema::Scope).apply {
                this.messageError = messagesErrorIdMapped
                    .firstOrNull { it.first == idMessageError || validators.size == 1 }?.second
                    ?: throw UiException.Default("Missing message error for validator")
            }.collect()
            @Suppress("UNCHECKED_CAST")
            validatorFactory.invoke(validatorPrototype) as? FieldValidatorProtocol<String>
        }?.takeIf { it.isNotEmpty() }
    }

    private val title
        get():String {
            return _title.value?.get(Language.Default.code)?.string ?: ""
        }

    private val placeholder
        get():String {
            return _placeholder.value?.get(Language.Default.code)?.string ?: ""
        }

    private var value
        get() = _value.value
        set(value) {
            _value.value = value
        }

    private var showError
        get() = _showError.value
        set(value) {
            _showError.value = value
        }

    private val messageErrorExtra
        get() = _messageErrorExtra.value?.get(Language.Default.code)?.string ?: ""

    @Composable
    override fun showComponent(scope: Any?) {
        //TODO validators:
        //  - when lost focus, if not empty test validator and update the error status
        //  - when gain focus and user write, remove the error while user is typing

        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = value,
            onValueChange = { newValue ->
                updateFieldForm.invoke(
                    url = url,
                    id = componentObject.idValue,
                    value = newValue,
                )
                showError = false
                value = newValue
            },
            label = {
                Text(title)
            },
            placeholder = {
                Text(placeholder)
            },
            singleLine = true,
            isError = showError,
            supportingText = {
                if (showError) {
                    Column {
                        validators?.filter { !it.isValid() }?.forEach {
                            Text(
                                //TODO language retrieve by Composition Local
                                it.getErrorMessage(Language.Default),
                                fontSize = 13.sp //TODO
                            )
                        }
                        messageErrorExtra?.let {
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