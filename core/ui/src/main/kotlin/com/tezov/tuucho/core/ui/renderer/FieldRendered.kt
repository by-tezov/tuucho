package com.tezov.tuucho.core.ui.renderer

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.tezov.tuucho.core.domain.protocol.ValidatorProtocol
import com.tezov.tuucho.core.domain.protocol.state.MaterialStateProtocol
import com.tezov.tuucho.core.domain.schema.ComponentSchema.Companion.contentObjectOrNull
import com.tezov.tuucho.core.domain.schema.ComponentSchema.Companion.optionObjectOrNull
import com.tezov.tuucho.core.domain.schema.ComponentSchema.Companion.styleObjectOrNull
import com.tezov.tuucho.core.domain.schema.IdSchema.Companion.id
import com.tezov.tuucho.core.domain.schema.SubsetSchema.Companion.subsetOrNull
import com.tezov.tuucho.core.domain.schema.TypeSchema
import com.tezov.tuucho.core.domain.schema.TypeSchema.Companion.typeOrNull
import com.tezov.tuucho.core.domain.schema.ValidatorSchema.Companion.validatorArrayOrNull
import com.tezov.tuucho.core.domain.schema._element.FieldSchema
import com.tezov.tuucho.core.domain.schema._element.FieldSchema.Content.placeholderObject
import com.tezov.tuucho.core.domain.schema._element.FieldSchema.Content.titleObject
import com.tezov.tuucho.core.domain.usecase.ValidatorFactoryUseCase
import com.tezov.tuucho.core.ui.renderer._system.ComposableScreenProtocol
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import com.tezov.tuucho.core.domain.schema.TextSchema.default as defaultText

class FieldRendered(
    private val materialState: MaterialStateProtocol,
    private val validatorFactory: ValidatorFactoryUseCase,
) : Renderer() {

    override fun accept(materialElement: JsonElement): Boolean {
        return materialElement.typeOrNull == TypeSchema.Value.Type.component &&
                materialElement.subsetOrNull == FieldSchema.Component.Value.subset
    }

    override fun process(materialElement: JsonElement): ComposableScreenProtocol {
        val content = materialElement.contentObjectOrNull
        val style = materialElement.styleObjectOrNull
        val option = materialElement.optionObjectOrNull

        @Suppress("UNCHECKED_CAST")
        val validators = option?.validatorArrayOrNull?.map{
            it.jsonObject.toMutableMap().apply {
                put("message-error", JsonObject( //TODO: not hard written please
                    mutableMapOf<String, JsonElement>().apply {
                        put("default", JsonPrimitive("default-error-message"))
                    }
                ))
            }.let(::JsonObject)
        }?.mapNotNull { validatorFactory.invoke(it) as? ValidatorProtocol<String> }

        materialState
            .form()
            .fieldsState()
            .addField(materialElement.id, "", validators)

        return FieldScreen(
            title = content?.titleObject?.defaultText ?: "",
            placeholder = content?.placeholderObject?.defaultText ?: "",
            validators = validators,
            onValueChanged = { _, newValue ->
                materialState
                    .form()
                    .fieldsState()
                    .updateField(materialElement.id, newValue)
                newValue
            }
        )
    }
}

class FieldScreen(
    var title: String,
    var placeholder: String,
    var validators: List<ValidatorProtocol<String>>?,
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
                    text.value = acceptedValue
                }
            },
            label = { Text(title) },
            placeholder = { Text(placeholder) },
            singleLine = true
        )
    }
}