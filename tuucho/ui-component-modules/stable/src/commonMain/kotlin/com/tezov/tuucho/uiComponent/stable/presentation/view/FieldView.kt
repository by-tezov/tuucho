package com.tezov.tuucho.uiComponent.stable.presentation.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.tezov.tuucho.core.domain.business.jsonSchema._element.form.FormSchema
import com.tezov.tuucho.core.domain.business.protocol.screen.view.FormStateProtocol
import com.tezov.tuucho.core.domain.tool.extension.ExtensionBoolean.isTrueOrNull
import com.tezov.tuucho.core.presentation.tool.modifier.then
import com.tezov.tuucho.core.presentation.ui._system.subset
import com.tezov.tuucho.core.presentation.ui.render.projection.TextProjectionProtocol
import com.tezov.tuucho.core.presentation.ui.render.projection.TextsProjectionProtocol
import com.tezov.tuucho.core.presentation.ui.render.projection.ValueStorageProjectionProtocol
import com.tezov.tuucho.core.presentation.ui.render.projection.form.FormStateProjectionProtocol
import com.tezov.tuucho.core.presentation.ui.render.projection.form.FormValidatorProjectionProtocol
import com.tezov.tuucho.core.presentation.ui.render.projection.form.field
import com.tezov.tuucho.core.presentation.ui.render.projection.form.validators
import com.tezov.tuucho.core.presentation.ui.render.projection.message.MessageTextProjectionProtocol
import com.tezov.tuucho.core.presentation.ui.render.projection.message.mutable
import com.tezov.tuucho.core.presentation.ui.render.projection.message.text
import com.tezov.tuucho.core.presentation.ui.render.projection.mutable
import com.tezov.tuucho.core.presentation.ui.render.projection.text
import com.tezov.tuucho.core.presentation.ui.render.projection.texts
import com.tezov.tuucho.core.presentation.ui.render.projector.componentProjector
import com.tezov.tuucho.core.presentation.ui.render.projector.content
import com.tezov.tuucho.core.presentation.ui.render.projector.contextual
import com.tezov.tuucho.core.presentation.ui.render.projector.message
import com.tezov.tuucho.core.presentation.ui.render.projector.option
import com.tezov.tuucho.core.presentation.ui.render.projector.state
import com.tezov.tuucho.core.presentation.ui.screen.protocol.ScreenContextProtocol
import com.tezov.tuucho.core.presentation.ui.view.AbstractView
import com.tezov.tuucho.core.presentation.ui.view.protocol.ViewFactoryProtocol
import com.tezov.tuucho.core.presentation.ui.view.protocol.ViewProtocol
import com.tezov.tuucho.uiComponent.stable.domain.jsonSchema.material.form.FormFieldSchema
import com.tezov.tuucho.uiComponent.stable.domain.jsonSchema.material.form.FormFieldSchema.Component
import kotlinx.serialization.json.JsonObject

interface FieldViewProtocol : ViewProtocol {
    @Composable
    fun ComposeComponent(
        scope: Any?,
        fieldValue: ValueStorageProjectionProtocol<String>,
        showError: MutableState<Boolean>,
        titleValue: String?,
        placeholderValue: String?,
        supportingTexts: List<String>?,
        messageErrorExtra: String?,
    )

    @Composable
    fun ComposePlaceHolder()
}

class FieldViewFactory : ViewFactoryProtocol {
    companion object {
        fun createFieldView(
            screenContext: ScreenContextProtocol,
        ): FieldViewProtocol = FieldView(
            screenContext = screenContext
        )
    }

    override fun accept(
        componentObject: JsonObject
    ) = componentObject.subset == Component.Value.subset

    override suspend fun process(
        screenContext: ScreenContextProtocol,
    ): FieldViewProtocol = createFieldView(
        screenContext = screenContext,
    )
}

private class FieldView(
    screenContext: ScreenContextProtocol,
) : AbstractView(screenContext),
    FieldViewProtocol,
    FormStateProtocol.Extension {
    private var showError = mutableStateOf(false)
    private lateinit var titleValue: TextProjectionProtocol
    private lateinit var placeholderValue: TextProjectionProtocol
    private lateinit var fieldValue: TextProjectionProtocol
    private lateinit var messageErrors: TextsProjectionProtocol
    private lateinit var messageErrorExtra: MessageTextProjectionProtocol
    private lateinit var validators: FormValidatorProjectionProtocol
    private lateinit var formState: FormStateProjectionProtocol

    override val extensionFormState get() = formState

    override suspend fun createComponentProjector() = componentProjector {
        +option {
            validators = +validators(FormSchema.Option.Key.validators)
        }.contextual
        +content {
            titleValue = +text(FormFieldSchema.Content.Key.title).mutable
            placeholderValue = +text(FormFieldSchema.Content.Key.placeholder).mutable
            messageErrors = +texts(FormFieldSchema.Content.Key.messageErrors)
        }.contextual
        +state {
            fieldValue = +text(FormFieldSchema.State.Key.initialValue).mutable
        }.contextual
        val message = +message(
            FormSchema.Message.Value.Subset.updateErrorState,
            onReceived = ::onReceivedUpdateErrorMessage
        ) {
            messageErrorExtra = +text(FormSchema.Message.Key.messageErrorExtra).mutable
        }
        formState = field().apply {
            messageLazyId = message.lazyId
            messageErrorProjection = messageErrors
            validatorProjection = validators
            fieldValueProjection = fieldValue
        }
    }.contextual

    private fun onReceivedUpdateErrorMessage() {
        showError.value = formState.isValid() == false || messageErrorExtra.value != null
    }

    override fun getResolvedStatus() = titleValue.hasBeenResolved.isTrueOrNull &&
        placeholderValue.hasBeenResolved.isTrueOrNull &&
        messageErrors.hasBeenResolved.isTrueOrNull &&
        messageErrorExtra.hasBeenResolved.isTrueOrNull &&
        validators.hasBeenResolved.isTrueOrNull

    @Composable
    override fun displayComponent(
        scope: Any?
    ) {
        ComposeComponent(
            scope = scope,
            fieldValue = fieldValue,
            showError = showError,
            titleValue = titleValue.value,
            placeholderValue = placeholderValue.value,
            supportingTexts = formState.supportingTexts.value,
            messageErrorExtra = messageErrorExtra.value
        )
    }

    @Composable
    override fun ComposeComponent(
        scope: Any?,
        fieldValue: ValueStorageProjectionProtocol<String>,
        showError: MutableState<Boolean>,
        titleValue: String?,
        placeholderValue: String?,
        supportingTexts: List<String>?,
        messageErrorExtra: String?,
    ) {
        // TODO validators:
        //  - when lost focus, if not empty test validator and update the error status
        //  - when gain focus and user write, remove the error while user is typing
        TextField(
            modifier = Modifier
                .then {
                    modifier = when (scope) {
                        is RowScope -> scope.run { modifier.weight(1.0f) }
                        else -> modifier.fillMaxWidth()
                    }
                },
            value = fieldValue.value ?: "",
            onValueChange = { newValue ->
                showError.value = false
                fieldValue.value = newValue
            },
            label = {
                titleValue?.let { Text(it) }
            },
            placeholder = {
                placeholderValue?.let { Text(it) }
            },
            singleLine = true,
            isError = showError.value,
            supportingText = {
                if (showError.value) {
                    Column {
                        supportingTexts?.forEach {
                            Text(
                                it,
                                fontSize = 13.sp // TODO
                            )
                        }
                        messageErrorExtra?.let {
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

    @Composable
    override fun ComposePlaceHolder() {
        displayPlaceholder(null)
    }
}
