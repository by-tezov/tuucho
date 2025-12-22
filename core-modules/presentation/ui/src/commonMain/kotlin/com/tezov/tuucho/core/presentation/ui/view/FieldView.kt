package com.tezov.tuucho.core.presentation.ui.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.tezov.tuucho.core.domain.business.jsonSchema.material._element.form.FormFieldSchema
import com.tezov.tuucho.core.domain.business.protocol.screen.view.FormStateProtocol
import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import com.tezov.tuucho.core.presentation.ui._system.subset
import com.tezov.tuucho.core.presentation.ui.render.protocol.ComponentProjectorProtocol
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
    override lateinit var componentProjector: ComponentProjectorProtocol

    private var showError by mutableStateOf(false)

//    private lateinit var fieldValue: TextProjection.Mutable
//    private lateinit var titleValue: TextProjection.Mutable
//    private lateinit var placeholderValue: TextProjection.Mutable
//    private lateinit var messageErrorExtra: TextMessageProjection.Mutable
//    private lateinit var validator: FormValidatorProjection
//    private lateinit var formState: FormStateProjection

    override val extensionFormState get() = TODO() // formState

    override fun updateReadyStatus() {
//        isReady = fieldValue.isReady.isTrueOrNull &&
//            titleValue.isReady.isTrueOrNull &&
//            placeholderValue.isReady.isTrueOrNull &&
//            messageErrorExtra.isReady.isTrueOrNull &&
//            validator.isReady.isTrueOrNull  &&
//            formState.isReady.isTrueOrNull
    }

    override suspend fun initProjection() {
//        componentProjector = componentProjector {
//            option {
//                validator {
//                    validator = projection(FormSchema.Option.Key.validator)
//                }
//            }
//            content {
//                text {
//                    titleValue = projection(FormFieldSchema.Content.Key.title)
//                    placeholderValue = projection(FormFieldSchema.Content.Key.placeholder)
//                }
//                field {
//                    formState = projection(FormFieldSchema.Content.Key.messageError)
//                }
//            }
//            state {
//                text {
//                    fieldValue = projection(FormFieldSchema.State.Key.initialValue)
//                }
//            }
//            message(FormSchema.Message.Value.Subset.updateErrorState) {
//                text {
//                    messageErrorExtra = projection(FormSchema.Message.Key.messageErrorExtra)
//                }
//            }
//        }
//        formState.apply {
//            componentId = componentObject.idValue
//            validatorProjection = validator
//            fieldValueProjection = fieldValue
//        }
//        messageErrorExtra.onReceived = {
//            showError = extensionFormState.isValid() == false || it != null
//        }
//        componentProjector.process(componentObject)
    }

    @Composable
    override fun displayComponent(
        scope: Any?
    ) {
        // TODO validators:
        //  - when lost focus, if not empty test validator and update the error status
        //  - when gain focus and user write, remove the error while user is typing

//        TextField(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 4.dp),
//            value = fieldValue.value ?: "",
//            onValueChange = { newValue ->
//                showError = false
//                fieldValue.value = newValue
//            },
//            label = {
//                titleValue.value?.let {
//                    Text(it)
//                }
//            },
//            placeholder = {
//                placeholderValue.value?.let {
//                    Text(it)
//                }
//            },
//            singleLine = true,
//            isError = showError,
//            supportingText = {
//                if (showError) {
//                    Column {
//                        formState.supportingTexts?.forEach {
//                            Text(
//                                it,
//                                fontSize = 13.sp // TODO
//                            )
//                        }
//                        messageErrorExtra.value?.let {
//                            Text(
//                                it,
//                                fontSize = 13.sp // TODO
//                            )
//                        }
//                    }
//                }
//            }
//        )
    }
}
