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
import com.tezov.tuucho.core.domain.schema.ActionSchema.Companion.actionObject
import com.tezov.tuucho.core.domain.schema.ActionSchema.Companion.paramsOrNull
import com.tezov.tuucho.core.domain.schema.ActionSchema.Companion.value
import com.tezov.tuucho.core.domain.schema.ComponentSchema.Companion.contentObject
import com.tezov.tuucho.core.domain.schema.ComponentSchema.Companion.styleObject
import com.tezov.tuucho.core.domain.schema.IdSchema.Companion.id
import com.tezov.tuucho.core.domain.schema.SubsetSchema.Companion.subsetOrNull
import com.tezov.tuucho.core.domain.schema.TypeSchema
import com.tezov.tuucho.core.domain.schema.TypeSchema.Companion.typeOrNull
import com.tezov.tuucho.core.domain.schema._element.ButtonSchema
import com.tezov.tuucho.core.domain.schema._element.ButtonSchema.Content.labelObject
import com.tezov.tuucho.core.domain.schema._element.LabelSchema.Content.valueObject
import com.tezov.tuucho.core.domain.schema._element.LabelSchema.Style.fontColorOrNull
import com.tezov.tuucho.core.domain.schema._element.LabelSchema.Style.fontSizeOrNull
import com.tezov.tuucho.core.domain.usecase.ActionHandlerUseCase
import com.tezov.tuucho.core.ui.renderer._system.ComposableScreenProtocol
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import org.koin.core.component.inject
import com.tezov.tuucho.core.domain.schema.ColorSchema.defaultOrNull as defaultColorOrNull
import com.tezov.tuucho.core.domain.schema.DimensionSchema.defaultOrNull as defaultDimensionOrNull
import com.tezov.tuucho.core.domain.schema.TextSchema.default as defaultText

class ButtonRendered : Renderer() {

    private val actionHandler: ActionHandlerUseCase by inject()

    override fun accept(materialElement: JsonElement): Boolean {
        return materialElement.typeOrNull == TypeSchema.Value.Type.component &&
                materialElement.subsetOrNull == ButtonSchema.Component.Value.subset
    }

    override fun process(materialElement: JsonElement): ComposableScreenProtocol {
        val content = materialElement.contentObject
        val style = materialElement.styleObject

        val labelComponent = content.labelObject
        val labelContent = labelComponent.contentObject
        val labelStyle = labelComponent.styleObject

        return ButtonScreen(
            text = labelContent.valueObject.defaultText,
            fontColor = labelStyle.fontColorOrNull?.defaultColorOrNull
                ?.runCatching { toColorInt().let(::Color) }
                ?.getOrNull(),
            fontSize = labelStyle.fontSizeOrNull?.defaultDimensionOrNull
                ?.toFloatOrNull()?.sp,
            action = {
                val action = content.actionObject
                actionHandler.invoke(
                    content.id,
                    action.value,
                    action.paramsOrNull?.mapValues { JsonPrimitive(it.value.string) }?.let(::JsonObject),
                )
            }
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