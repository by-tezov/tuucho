package com.tezov.tuucho.core.ui.renderer

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
import com.tezov.tuucho.core.domain.schema._element.label.LabelSchema
import com.tezov.tuucho.core.domain.schema.common.SubsetSchema.Companion.subsetOrNull
import com.tezov.tuucho.core.domain.schema.common.TypeSchema
import com.tezov.tuucho.core.domain.schema.common.TypeSchema.Companion.typeOrNull
import com.tezov.tuucho.core.ui.renderer._system.ComposableScreenProtocol
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject

class LabelRendered : Renderer() {

    override fun accept(jsonObject: JsonObject): Boolean {
        return jsonObject.typeOrNull == TypeSchema.Value.Type.component &&
                jsonObject.subsetOrNull == LabelSchema.Component.Value.subset
    }

    override fun process(jsonObject: JsonObject): ComposableScreenProtocol {
        val content = jsonObject[ComponentSchema.Key.content]!!.jsonObject
        val style = jsonObject[ComponentSchema.Key.style]!!.jsonObject

        val value = content[LabelSchema.Content.Key.value]!!.jsonObject
        val valueDefault = value[TextSchema.Key.default].string

        val fontColor = style[LabelSchema.Style.Key.fontColor] as? JsonObject
        val fontColorDefault = fontColor?.get(ColorSchema.Key.default)?.stringOrNull

        val fontSize = style[LabelSchema.Style.Key.fontSize] as? JsonObject
        val fontSizeDefault = fontSize?.get(DimensionSchema.Key.default)?.stringOrNull

        return LabelScreen(
            text = valueDefault,
            fontColor = fontColorDefault
                ?.runCatching { toColorInt().let(::Color) }
                ?.getOrNull(),
            fontSize = fontSizeDefault
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