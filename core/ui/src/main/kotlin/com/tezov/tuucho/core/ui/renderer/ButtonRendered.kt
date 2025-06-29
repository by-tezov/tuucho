package com.tezov.tuucho.core.ui.renderer

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.tezov.tuucho.core.domain._system.string
import com.tezov.tuucho.core.domain._system.stringOrNull
import com.tezov.tuucho.core.domain.schema.ColorSchema
import com.tezov.tuucho.core.domain.schema.ComponentSchema
import com.tezov.tuucho.core.domain.schema.DimensionSchema
import com.tezov.tuucho.core.domain.schema.TextSchema
import com.tezov.tuucho.core.domain.schema._element.button.ButtonSchema
import com.tezov.tuucho.core.domain.schema._element.label.LabelSchema
import com.tezov.tuucho.core.domain.schema.common.IdSchema.Companion.id
import com.tezov.tuucho.core.domain.schema.common.SubsetSchema.Companion.subsetOrNull
import com.tezov.tuucho.core.domain.schema.common.TypeSchema
import com.tezov.tuucho.core.domain.schema.common.TypeSchema.Companion.typeOrNull
import com.tezov.tuucho.core.domain.usecase.ActionHandlerUseCase
import com.tezov.tuucho.core.ui.renderer._system.ComposableScreenProtocol
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import org.koin.core.component.inject

class ButtonRendered : Renderer() {

    private val actionHandler: ActionHandlerUseCase by inject()

    override fun accept(jsonObject: JsonObject): Boolean {
        return jsonObject.typeOrNull == TypeSchema.Value.Type.component &&
                jsonObject.subsetOrNull == ButtonSchema.Component.Value.subset
    }

    override fun process(jsonObject: JsonObject): ComposableScreenProtocol {
        val content = jsonObject[ComponentSchema.Key.content]!!.jsonObject
        val style = jsonObject[ComponentSchema.Key.style]!!.jsonObject

        val value = content[ButtonSchema.Content.Key.value]!!.jsonObject
        val text = value[TextSchema.Key.default].string
        val action = content[ButtonSchema.Content.Key.action].string

        val labelStyle = style[ButtonSchema.Style.Key.label] as? JsonObject
        val fontColor = labelStyle?.get(LabelSchema.Style.Key.fontColor) as? JsonObject
        val fontColorDefault = fontColor?.get(ColorSchema.Key.default)?.stringOrNull

        val fontSize = labelStyle?.get(LabelSchema.Style.Key.fontSize) as? JsonObject
        val fontSizeDefault = fontSize?.get(DimensionSchema.Key.default)?.stringOrNull

        return ButtonScreen(
            text = text,
            action = { actionHandler.invoke(content.id, action) }
        )
    }
}

class ButtonScreen(
    var text: String,
    var action: () -> Unit
) : ComposableScreenProtocol() {

    @Composable
    override fun show(scope: Any?) {
        Button(
            onClick = action,
            content = { Text(text = text) }
        )
    }
}