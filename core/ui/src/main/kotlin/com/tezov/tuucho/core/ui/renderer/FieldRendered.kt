package com.tezov.tuucho.core.ui.renderer

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.tezov.tuucho.core.domain.protocol.state.MaterialStateProtocol
import com.tezov.tuucho.core.domain.schema.ComponentSchema.Companion.contentObjectOrNull
import com.tezov.tuucho.core.domain.schema.ComponentSchema.Companion.styleObjectOrNull
import com.tezov.tuucho.core.domain.schema.IdSchema.Companion.id
import com.tezov.tuucho.core.domain.schema.SubsetSchema.Companion.subsetOrNull
import com.tezov.tuucho.core.domain.schema.TypeSchema
import com.tezov.tuucho.core.domain.schema.TypeSchema.Companion.typeOrNull
import com.tezov.tuucho.core.domain.schema._element.FieldSchema
import com.tezov.tuucho.core.domain.schema._element.FieldSchema.Content.placeholderObject
import com.tezov.tuucho.core.domain.schema._element.FieldSchema.Content.titleObject
import com.tezov.tuucho.core.ui.renderer._system.ComposableScreenProtocol
import kotlinx.serialization.json.JsonElement
import com.tezov.tuucho.core.domain.schema.TextSchema.default as defaultText

class FieldRendered(
    private val materialState: MaterialStateProtocol
) : Renderer() {

    override fun accept(materialElement: JsonElement): Boolean {
        return materialElement.typeOrNull == TypeSchema.Value.Type.component &&
                materialElement.subsetOrNull == FieldSchema.Component.Value.subset
    }

    override fun process(materialElement: JsonElement): ComposableScreenProtocol {
        val content = materialElement.contentObjectOrNull
        val style = materialElement.styleObjectOrNull

//        val option = materialElement.optionObjectOrNull //TODO

        return FieldScreen(
            title = content?.titleObject?.defaultText ?: "",
            placeholder = content?.placeholderObject?.defaultText ?: "",
            onValueChanged = { _, newValue ->
                materialState
                    .form()
                    .fieldsState()
                    .addOrUpdateField(materialElement.id, newValue)
                newValue
            }
        )
    }
}

class FieldScreen(
    var title: String,
    var placeholder: String,
    var onValueChanged: (previousValue: String, newValue: String) -> String?,
) : ComposableScreenProtocol() {

    @Composable
    override fun show(scope: Any?) {
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