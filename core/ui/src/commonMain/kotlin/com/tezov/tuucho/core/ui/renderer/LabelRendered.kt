package com.tezov.tuucho.core.ui.renderer

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tezov.tuucho.core.domain._system.string
import com.tezov.tuucho.core.domain.config.Language
import com.tezov.tuucho.core.domain.model.schema._system.onScope
import com.tezov.tuucho.core.domain.model.schema._system.withScope
import com.tezov.tuucho.core.domain.model.schema.material.ColorSchema
import com.tezov.tuucho.core.domain.model.schema.material.DimensionSchema
import com.tezov.tuucho.core.domain.model.schema.material.IdSchema
import com.tezov.tuucho.core.domain.model.schema.material.SubsetSchema
import com.tezov.tuucho.core.domain.model.schema.material.TypeSchema
import com.tezov.tuucho.core.domain.model.schema.material._element.LabelSchema
import com.tezov.tuucho.core.domain.usecase.GetLanguageUseCase
import com.tezov.tuucho.core.ui._system.toColorOrNull
import com.tezov.tuucho.core.ui.renderer._system.ComposableScreenProtocol
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

class LabelRendered(
    private val getLanguage: GetLanguageUseCase
) : Renderer() {

    override fun accept(element: JsonElement) = element
        .let {
            it.withScope(TypeSchema::Scope).self == TypeSchema.Value.component &&
                    it.withScope(SubsetSchema::Scope).self == LabelSchema.Component.Value.subset
        }

    override fun process(element: JsonElement): ComposableScreenProtocol {
        val content = element.onScope(LabelSchema.Content::Scope)
        val style = element.onScope(LabelSchema.Style::Scope)

        val fontColor = style.fontColor
            ?.withScope(ColorSchema::Scope)?.default //TODO manage "selector",not only default
        val fontSize = style.fontSize
            ?.withScope(DimensionSchema::Scope)?.default //TODO manage "selector",not only default


        if (element.onScope(IdSchema::Scope).source != null
            || content.onScope(IdSchema::Scope).source != null  //TODO
            || content.value?.onScope(IdSchema::Scope)?.source != null
        ) {
            return object : ComposableScreenProtocol() {
                @Composable
                override fun show(scope: Any?) {
                    Box(
                        modifier = Modifier
                            .height(24.dp)
                            .fillMaxWidth()
                            .clip(CircleShape)
                            .shimmerLoading()
                    )
                }
            }
        }
        else {
            return LabelScreen(
                text = content.value,
                language = getLanguage.invoke(),
                fontColor = fontColor?.toColorOrNull(),
                fontSize = fontSize?.toFloatOrNull()?.sp
            )
        }
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


@Composable //TODO remove that and DO really the UI part
fun Modifier.shimmerLoading(
    durationMillis: Int = 2000,
): Modifier {
    val transition = rememberInfiniteTransition()

    val translateAnimation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 500f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = durationMillis,
                easing = LinearEasing,
            ),
            repeatMode = RepeatMode.Restart,
        )
    )

    return drawBehind {
        drawRect(
            brush = Brush.linearGradient(
                colors = persistentListOf(
                    Color.LightGray.copy(alpha = 0.2f),
                    Color.LightGray.copy(alpha = 1.0f),
                    Color.LightGray.copy(alpha = 0.2f),
                ),
                start = Offset(x = translateAnimation, y = translateAnimation),
                end = Offset(x = translateAnimation + 100f, y = translateAnimation + 100f),
            )
        )
    }
}