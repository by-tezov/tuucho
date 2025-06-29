package com.tezov.tuucho.core.ui.renderer

import androidx.compose.material3.Button
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
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

        val labelComponent = content[ButtonSchema.Content.Key.label]!!.jsonObject
        val labelContent = labelComponent[ComponentSchema.Key.content]!!.jsonObject
        val labelStyle = labelComponent[ComponentSchema.Key.style]!!.jsonObject

        val labelValue = labelContent[LabelSchema.Content.Key.value]!!.jsonObject
        val labelValueDefault = labelValue[TextSchema.Key.default].string

        println(labelStyle)


        val fontColor = labelStyle[LabelSchema.Style.Key.fontColor] as? JsonObject
        val fontColorDefault = fontColor?.get(ColorSchema.Key.default)?.stringOrNull

        val fontSize = labelStyle[LabelSchema.Style.Key.fontSize] as? JsonObject
        val fontSizeDefault = fontSize?.get(DimensionSchema.Key.default)?.stringOrNull


        val action = content[ButtonSchema.Content.Key.action].string

        return ButtonScreen(
            text = labelValueDefault,
            fontColor = fontColorDefault
                ?.runCatching { toColorInt().let(::Color) }
                ?.getOrNull(),
            fontSize = fontSizeDefault
                ?.toFloatOrNull()?.sp,
            action = { actionHandler.invoke(content.id, action) }
        )
    }
}

class ButtonScreen(
    var text: String,
    var fontColor: Color?,
    var fontSize: TextUnit?,
    var action: () -> Unit
) : ComposableScreenProtocol() {

    @Composable
    override fun show(scope: Any?) {
        val textStyle = LocalTextStyle.current.let { current ->
            current.copy(
                color = if (fontColor != null) fontColor!!
                else current.color,
                fontSize = if (fontSize != null) fontSize!!
                else current.fontSize,
            )
        }
        Button(
            onClick = action,
            content = {
                Text(
                    text = text,
                    style = textStyle
                )
            }
        )
    }
}