package com.tezov.tuucho.core.ui.renderer

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import com.tezov.tuucho.core.domain._system.string
import com.tezov.tuucho.core.domain.config.Language
import com.tezov.tuucho.core.domain.model.schema._system.Schema.Companion.schema
import com.tezov.tuucho.core.domain.model.schema.material.ColorSchema
import com.tezov.tuucho.core.domain.model.schema.material.DimensionSchema
import com.tezov.tuucho.core.domain.model.schema.material.SubsetSchema
import com.tezov.tuucho.core.domain.model.schema.material.TypeSchema
import com.tezov.tuucho.core.domain.model.schema.material._element.LabelSchema
import com.tezov.tuucho.core.domain.usecase.GetLanguageUseCase
import com.tezov.tuucho.core.ui.renderer._system.ComposableScreenProtocol
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

class LabelRendered(
    private val getLanguage: GetLanguageUseCase
) : Renderer() {

    override fun accept(element: JsonElement) = element.schema()
        .let {
            it.withScope(TypeSchema::Scope).self == TypeSchema.Value.component &&
            it.withScope(SubsetSchema::Scope).self == LabelSchema.Component.Value.subset
        }

    override fun process(element: JsonElement): ComposableScreenProtocol {
        val schema = element.schema()
        val content = schema.onScope(LabelSchema.Content::Scope)
        val style = schema.onScope(LabelSchema.Style::Scope)

        val fontColor = style.fontColor?.schema()
            ?.withScope(ColorSchema::Scope)?.default //TODO manage "selector",not only default
        val fontSize = style.fontSize?.schema()
            ?.withScope(DimensionSchema::Scope)?.default //TODO manage "selector",not only default

        return LabelScreen(
            text = content.value,
            language = getLanguage.invoke(),
            fontColor = fontColor
                ?.runCatching { toColorInt().let(::Color) }
                ?.getOrNull(),
            fontSize = fontSize?.toFloatOrNull()?.sp
        )
    }
}

class LabelScreen(
    var text: JsonObject?,
    var language: Language,
    var fontColor: Color?,
    var fontSize: TextUnit?,
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
            text = text?.get(language.code)?.string ?: "",
            style = textStyle
        )
    }

}