package com.tezov.tuucho.core.presentation.ui.renderer.view.fieldView

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tezov.tuucho.core.domain.business.config.Language
import com.tezov.tuucho.core.domain.business.model.schema._system.withScope
import com.tezov.tuucho.core.domain.business.model.schema.material.ComponentSchema
import com.tezov.tuucho.core.domain.business.model.schema.material.ComponentSchema.contentOrNull
import com.tezov.tuucho.core.domain.business.model.schema.material.ComponentSchema.optionOrNull
import com.tezov.tuucho.core.domain.business.model.schema.material.IdSchema.idValue
import com.tezov.tuucho.core.domain.business.model.schema.material.MessageSchema
import com.tezov.tuucho.core.domain.business.model.schema.material.SubsetSchema
import com.tezov.tuucho.core.domain.business.model.schema.material.TypeSchema
import com.tezov.tuucho.core.domain.business.model.schema.material._element.form.FormFieldSchema
import com.tezov.tuucho.core.domain.business.model.schema.material._element.form.FormSchema
import com.tezov.tuucho.core.domain.business.protocol.screen.view.form.FieldFormViewProtocol
import com.tezov.tuucho.core.domain.business.usecase.FormValidatorFactoryUseCase
import com.tezov.tuucho.core.domain.business.usecase._system.UseCaseExecutor
import com.tezov.tuucho.core.domain.tool.json.string
import com.tezov.tuucho.core.presentation.ui.renderer.screen.ScreenIdentifier
import com.tezov.tuucho.core.presentation.ui.renderer.view.View
import com.tezov.tuucho.core.presentation.ui.renderer.view._system.ViewFactory
import com.tezov.tuucho.core.presentation.ui.renderer.view._system.ViewIdentifier
import kotlinx.serialization.json.JsonObject

class FieldViewFactory(
    private val identifierFactory: (screenIdentifier: ScreenIdentifier) -> ViewIdentifier,
    private val useCaseExecutor: UseCaseExecutor,
    private val fieldValidatorFactory: FormValidatorFactoryUseCase,
) : ViewFactory() {

    override fun accept(componentElement: JsonObject) = componentElement.let {
        it.withScope(TypeSchema::Scope).self == TypeSchema.Value.component &&
                it.withScope(SubsetSchema::Scope).self == FormFieldSchema.Component.Value.subset
    }

    override suspend fun process(
        screenIdentifier: ScreenIdentifier,
        componentObject: JsonObject,
    ) = FieldView(
        identifier = identifierFactory.invoke(screenIdentifier),
        componentObject = componentObject,
        fieldFormView = FieldFormView(
            useCaseExecutor = useCaseExecutor,
            fieldValidatorFactory = fieldValidatorFactory,
        )
    ).also { it.init() }
}

class FieldView(
    identifier: ViewIdentifier,
    componentObject: JsonObject,
    fieldFormView: FieldFormView,
) : View(identifier, componentObject), FieldFormViewProtocol.Extension {

    override val formView = fieldFormView.also { it.attach(this) }

    private val _title = mutableStateOf<JsonObject?>(null)
    private val _placeholder = mutableStateOf<JsonObject?>(null)
    private val _value = mutableStateOf("")
    private val _showError = mutableStateOf(false)
    private val _messageErrorExtra = mutableStateOf<JsonObject?>(null)

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

    override suspend fun JsonObject.processComponent() {
        withScope(ComponentSchema::Scope).run {
            content?.processContent()
            option?.processOption()
            state?.processState()
        }
    }

    override suspend fun JsonObject.processContent() {
        withScope(FormFieldSchema.Content::Scope).run {
            title?.processText(FormFieldSchema.Content.Key.title)
            placeholder?.processText(FormFieldSchema.Content.Key.placeholder)
            messageError?.let { processValidator() }
        }
    }

    override suspend fun JsonObject.processOption() {
        componentObject.contentOrNull
            ?.withScope(FormFieldSchema.Content::Scope)
            ?.messageError?.let { processValidator() }
    }

    override suspend fun JsonObject.processState() {
        withScope(FormSchema.State::Scope).run {
            initialValue?.get(Language.Default.code)?.string?.let {
                _value.value = it
            }
        }
    }

    override suspend fun JsonObject.processText(key: String) {
        when (key) {
            FormFieldSchema.Content.Key.title -> _title.value = this
            FormFieldSchema.Content.Key.placeholder -> _placeholder.value = this
        }
    }

    override suspend fun JsonObject.processMessage() {
        when (withScope(MessageSchema::Scope).subset) {
            FormSchema.Message.Value.Subset.updateErrorState -> {
                withScope(FormSchema.Message::Scope).run {
                    val isValid = formView.isValid()
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

    private suspend fun processValidator() {
        componentObject.optionOrNull?.withScope(FormSchema.Option::Scope)?.run {
            validator?.let { validators ->
                val messageError = componentObject.contentOrNull
                    ?.withScope(FormFieldSchema.Content::Scope)
                    ?.messageError

                messageError?.let {
                    formView.buildValidator(validators, messageError)

                }
            }
        }
    }

    private val title
        get():String {
            return _title.value?.get(Language.Default.code)?.string ?: ""
        }

    private val placeholder
        get():String {
            return _placeholder.value?.get(Language.Default.code)?.string ?: ""
        }

    var value
        get() = _value.value
        set(value) {
            _value.value = value
            _messageErrorExtra.value = null
        }

    private var showError
        get() = _showError.value
        set(value) {
            _showError.value = value
        }

    private val messageErrorExtra
        get() = _messageErrorExtra.value?.get(Language.Default.code)?.string

    @Composable
    override fun displayComponent(scope: Any?) {
        //TODO validators:
        //  - when lost focus, if not empty test validator and update the error status
        //  - when gain focus and user write, remove the error while user is typing

        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            value = value,
            onValueChange = { newValue ->
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
                        formView.validators?.filter { !it.isValid() }?.forEach {
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

