package com.tezov.tuucho.core.ui.renderer

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import com.tezov.tuucho.core.domain.schema.ComponentSchema.Companion.contentObjectOrNull
import com.tezov.tuucho.core.domain.schema.ComponentSchema.Companion.styleObjectOrNull
import com.tezov.tuucho.core.domain.schema.SubsetSchema.Companion.subsetOrNull
import com.tezov.tuucho.core.domain.schema.TypeSchema
import com.tezov.tuucho.core.domain.schema.TypeSchema.Companion.typeOrNull
import com.tezov.tuucho.core.domain.schema._element.LabelSchema
import com.tezov.tuucho.core.domain.schema._element.LabelSchema.Content.valueObject
import com.tezov.tuucho.core.domain.schema._element.LabelSchema.Style.fontColorOrNull
import com.tezov.tuucho.core.domain.schema._element.LabelSchema.Style.fontSizeOrNull
import com.tezov.tuucho.core.ui.renderer._system.ComposableScreenProtocol
import kotlinx.serialization.json.JsonElement
import com.tezov.tuucho.core.domain.schema.ColorSchema.defaultOrNull as defaultColorOrNull
import com.tezov.tuucho.core.domain.schema.DimensionSchema.defaultOrNull as defaultDimensionOrNull
import com.tezov.tuucho.core.domain.schema.TextSchema.default as defaultText

class LabelRendered : Renderer() {

    override fun accept(materialElement: JsonElement): Boolean {
        return materialElement.typeOrNull == TypeSchema.Value.Type.component &&
                materialElement.subsetOrNull == LabelSchema.Component.Value.subset
    }

    override fun process(materialElement: JsonElement): ComposableScreenProtocol {
        val content = materialElement.contentObjectOrNull
        val style = materialElement.styleObjectOrNull

        return LabelScreen(
            text = content?.valueObject?.defaultText?: "",
            fontColor = style?.fontColorOrNull?.defaultColorOrNull
                ?.runCatching { toColorInt().let(::Color) }
                ?.getOrNull(),
            fontSize = style?.fontSizeOrNull?.defaultDimensionOrNull
                ?.toFloatOrNull()?.sp
        )
    }
}

class LabelScreen(
    var text: String,
    var fontColor: Color?,
    var fontSize: TextUnit?
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
        Text(
            text = text,
            style = textStyle
        )
    }

}