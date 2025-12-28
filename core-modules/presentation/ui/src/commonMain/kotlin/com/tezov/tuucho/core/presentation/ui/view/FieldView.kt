package com.tezov.tuucho.core.presentation.ui.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tezov.tuucho.core.domain.business.jsonSchema.material._element.form.FormFieldSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material._element.form.FormSchema
import com.tezov.tuucho.core.domain.business.protocol.screen.view.FormStateProtocol
import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import com.tezov.tuucho.core.presentation.ui._system.idValue
import com.tezov.tuucho.core.presentation.ui._system.subset
import com.tezov.tuucho.core.presentation.ui.render.projectable.field
import com.tezov.tuucho.core.presentation.ui.render.projectable.projection
import com.tezov.tuucho.core.presentation.ui.render.projectable.text
import com.tezov.tuucho.core.presentation.ui.render.projectable.validator
import com.tezov.tuucho.core.presentation.ui.render.projection.FormStateProjectionProtocol
import com.tezov.tuucho.core.presentation.ui.render.projection.FormValidatorProjectionProtocol
import com.tezov.tuucho.core.presentation.ui.render.projection.MessageTextProjectionProtocol
import com.tezov.tuucho.core.presentation.ui.render.projection.TextProjectionProtocol
import com.tezov.tuucho.core.presentation.ui.render.projector.componentProjector
import com.tezov.tuucho.core.presentation.ui.render.projector.content
import com.tezov.tuucho.core.presentation.ui.render.projector.message
import com.tezov.tuucho.core.presentation.ui.render.projector.option
import com.tezov.tuucho.core.presentation.ui.render.projector.state
import com.tezov.tuucho.core.presentation.ui.screen.Screen
import com.tezov.tuucho.core.presentation.ui.view._system.ViewFactoryProtocol
import kotlinx.serialization.json.JsonObject

class FieldViewFactory : ViewFactoryProtocol {
    override fun accept(
        componentObject: JsonObject
    ) = componentObject.subset == FormFieldSchema.Component.Value.subset

    override suspend fun process(
        screen: Screen,
        path: JsonElementPath,
    ) = FieldView(
        screen = screen,
        path = path
    ).also { it.init() }
}

class FieldView(
    screen: Screen,
    path: JsonElementPath,
) : AbstractView(screen, path),
    FormStateProtocol.Extension {

    private var showError by mutableStateOf(false)

    private lateinit var fieldValue: TextProjectionProtocol
    private lateinit var titleValue: TextProjectionProtocol
    private lateinit var placeholderValue: TextProjectionProtocol
    private lateinit var messageErrorExtra: MessageTextProjectionProtocol
    private lateinit var validator: FormValidatorProjectionProtocol
    private lateinit var formState: FormStateProjectionProtocol

    override val extensionFormState get() = formState

    override suspend fun createComponentProjectorProjection() = componentProjector(contextual = true) {
        option {
            validator {
                validator = projection(FormSchema.Option.Key.validator)
            }
        }
        content(contextual = true) {
            text {
                titleValue = projection(FormFieldSchema.Content.Key.title)
                placeholderValue = projection(FormFieldSchema.Content.Key.placeholder)
            }
            field {
                formState = projection(FormFieldSchema.Content.Key.messageError)
            }
        }
        state(contextual = true) {
            text {
                fieldValue = projection(FormFieldSchema.State.Key.initialValue, mutable = true, contextual = true)
            }
        }
        message(FormSchema.Message.Value.Subset.updateErrorState) {
            text {
                messageErrorExtra = projection(FormSchema.Message.Key.messageErrorExtra)
            }
        }
    }.also {
        formState.apply {
            componentId = componentObject.idValue
            validatorProjection = validator
            fieldValueProjection = fieldValue
        }
        messageErrorExtra.apply {
            componentId = formState.getId()
            onReceived = {
                showError = extensionFormState.isValid() == false || it != null
            }
        }
    }

    @Composable
    override fun displayComponent(
        scope: Any?
    ) {
        // TODO validators:
        //  - when lost focus, if not empty test validator and update the error status
        //  - when gain focus and user write, remove the error while user is typing

        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            value = fieldValue.value ?: "",
            onValueChange = { newValue ->
                showError = false
                fieldValue.value = newValue
            },
            label = {
                titleValue.value?.let {
                    Text(it)
                }
            },
            placeholder = {
                placeholderValue.value?.let {
                    Text(it)
                }
            },
            singleLine = true,
            isError = showError,
            supportingText = {
                if (showError) {
                    Column {
                        formState.supportingTexts?.forEach {
                            Text(
                                it,
                                fontSize = 13.sp // TODO
                            )
                        }
                        messageErrorExtra.value?.let {
                            Text(
                                it,
                                fontSize = 13.sp // TODO
                            )
                        }
                    }
                }
            }
        )
    }
}
