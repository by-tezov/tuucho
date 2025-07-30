package com.tezov.tuucho.core.ui.composable

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.tezov.tuucho.core.domain._system.string
import com.tezov.tuucho.core.domain.model.schema._system.onScope
import com.tezov.tuucho.core.domain.model.schema._system.withScope
import com.tezov.tuucho.core.domain.model.schema.material.ColorSchema
import com.tezov.tuucho.core.domain.model.schema.material.ComponentSchema
import com.tezov.tuucho.core.domain.model.schema.material.DimensionSchema
import com.tezov.tuucho.core.domain.model.schema.material.IdSchema
import com.tezov.tuucho.core.domain.model.schema.material.SubsetSchema
import com.tezov.tuucho.core.domain.model.schema.material.TypeSchema
import com.tezov.tuucho.core.domain.model.schema.material._element.LabelSchema
import com.tezov.tuucho.core.ui._system.toColorOrNull
import com.tezov.tuucho.core.ui.composable._system.Screen
import com.tezov.tuucho.core.ui.composable._system.UiComponentFactory
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject

class LabelRendered() : UiComponentFactory() {

    override fun accept(componentElement: JsonObject) = componentElement.let {
        it.withScope(TypeSchema::Scope).self == TypeSchema.Value.component &&
                it.withScope(SubsetSchema::Scope).self == LabelSchema.Component.Value.subset
    }

    override fun process(componentElement: JsonObject) = LabelScreen(componentElement)
}

class LabelScreen(
    componentElement: JsonObject
) : Screen() {

    private val _text = mutableStateOf<JsonObject?>(null)
    private var _fontColor: JsonObject? = null
    private var _fontSize: JsonObject? = null

    init {
        val componentScope = componentElement.withScope(ComponentSchema::Scope)
        val contentScope = componentScope.content?.withScope(LabelSchema.Content::Scope)

        componentScope.onScope(IdSchema::Scope).value?.let {
            addProcessor(TypeSchema.Value.component, id = it, ::processComponent)
        }
        componentScope.style?.onScope(IdSchema::Scope)?.value?.let {
            addProcessor(TypeSchema.Value.style, id = it, ::processStyle)
        }
        contentScope?.onScope(IdSchema::Scope)?.value?.let {
            addProcessor(TypeSchema.Value.content, id = it, ::processContent)
        }
        contentScope?.value?.onScope(IdSchema::Scope)?.value?.let {
            addProcessor(TypeSchema.Value.text, id = it, ::processText)
        }

        processComponent(componentElement)
        //TODO if can't be renderer, show a skimmer
    }

    private fun processComponent(componentElement: JsonObject) {
        val componentScope = componentElement.withScope(ComponentSchema::Scope)
        componentScope.style?.let { processStyle(it) }
        componentScope.content?.let { processContent(it) }
    }

    private fun processStyle(styleElement: JsonObject) {
        val styleScope = styleElement.withScope(LabelSchema.Style::Scope)
        styleScope.fontColor?.let { _fontColor = it }
        styleScope.fontSize?.let { _fontSize = it }
    }

    private fun processContent(contentElement: JsonObject) {
        val contentScope = contentElement.withScope(LabelSchema.Content::Scope)
        contentScope.value?.let { processText(it) }
    }

    private fun processText(textElement: JsonObject) {
        _text.value = textElement.jsonObject
    }

    private val text
        @Composable get():String {
            //TODO language retrieve by Composition Local
            return _text.value?.get("default")?.string ?: ""
        }

    private val fontColor
        @Composable get():Color? {
            //TODO default selector ? how to do that ?
            return _fontColor?.withScope(ColorSchema::Scope)
                ?.default?.toColorOrNull()
        }

    private val fontSize
        @Composable get():TextUnit? {
            //TODO default selector ? how to do that ?
            return _fontSize?.withScope(DimensionSchema::Scope)
                ?.default?.toFloatOrNull()?.sp
        }

    @Composable
    override fun show(scope: Any?) {
        val textStyle = LocalTextStyle.current.let { current ->
            current.copy(
                color = fontColor ?: current.color,
                fontSize = fontSize ?: current.fontSize,
            )
        }
        Text(
            text = text,
            style = textStyle
        )
    }
}


//@Composable //TODO remove that and DO really the UI part
//fun Modifier.shimmerLoading(
//    durationMillis: Int = 2000,
//): Modifier {
//    val transition = rememberInfiniteTransition()
//
//    val translateAnimation by transition.animateFloat(
//        initialValue = 0f,
//        targetValue = 500f,
//        animationSpec = infiniteRepeatable(
//            animation = tween(
//                durationMillis = durationMillis,
//                easing = LinearEasing,
//            ),
//            repeatMode = RepeatMode.Restart,
//        )
//    )
//
//    return drawBehind {
//        drawRect(
//            brush = Brush.linearGradient(
//                colors = persistentListOf(
//                    Color.LightGray.copy(alpha = 0.2f),
//                    Color.LightGray.copy(alpha = 1.0f),
//                    Color.LightGray.copy(alpha = 0.2f),
//                ),
//                start = Offset(x = translateAnimation, y = translateAnimation),
//                end = Offset(x = translateAnimation + 100f, y = translateAnimation + 100f),
//            )
//        )
//    }
//}