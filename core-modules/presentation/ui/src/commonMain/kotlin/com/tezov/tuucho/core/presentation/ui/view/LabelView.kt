package com.tezov.tuucho.core.presentation.ui.view

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.tezov.tuucho.core.domain.business.jsonSchema.material._element.LabelSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material._element.LabelSchema.Content
import com.tezov.tuucho.core.domain.business.jsonSchema.material._element.LabelSchema.Style
import com.tezov.tuucho.core.domain.tool.extension.ExtensionBoolean.isTrueOrNull
import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import com.tezov.tuucho.core.presentation.ui._system.subset
import com.tezov.tuucho.core.presentation.ui.render.projectable.color
import com.tezov.tuucho.core.presentation.ui.render.projectable.dimension
import com.tezov.tuucho.core.presentation.ui.render.projectable.projection
import com.tezov.tuucho.core.presentation.ui.render.projectable.text
import com.tezov.tuucho.core.presentation.ui.render.projection.ColorProjection
import com.tezov.tuucho.core.presentation.ui.render.projection.SpProjection
import com.tezov.tuucho.core.presentation.ui.render.projection.TextProjection
import com.tezov.tuucho.core.presentation.ui.render.projector.componentProjector
import com.tezov.tuucho.core.presentation.ui.render.projector.content
import com.tezov.tuucho.core.presentation.ui.render.projector.style
import com.tezov.tuucho.core.presentation.ui.render.protocol.ComponentProjectorProtocol
import com.tezov.tuucho.core.presentation.ui.screen.Screen
import com.tezov.tuucho.core.presentation.ui.view._system.ViewFactoryProtocol
import kotlinx.serialization.json.JsonObject

class LabelViewFactory : ViewFactoryProtocol {
    override fun accept(
        componentObject: JsonObject
    ) = componentObject.subset == LabelSchema.Component.Value.subset

    override suspend fun process(
        screen: Screen,
        path: JsonElementPath,
    ) = LabelView(
        screen = screen,
        path = path,
    ).also { it.init() }
}

class LabelView(
    screen: Screen,
    path: JsonElementPath,
) : AbstractView(screen, path) {

    override lateinit var componentProjector: ComponentProjectorProtocol

    private lateinit var textValue: TextProjection.Mutable
    private lateinit var fontColor: ColorProjection.Static
    private lateinit var fontSize: SpProjection.Static

    override fun updateReadyStatus() {
        isReady = textValue.isReady.isTrueOrNull &&
            fontColor.isReady.isTrueOrNull &&
            fontSize.isReady.isTrueOrNull
    }

    override suspend fun initProjection() {
        componentProjector = componentProjector {
            style {
                color {
                    fontColor = projection(Style.Key.fontColor)
                }
                dimension {
                    fontSize = projection(Style.Key.fontSize)
                }
            }
            content {
                text {
                    textValue = projection(Content.Key.value)
                }
            }
        }
        componentProjector.process(componentObject)



    }

    @Composable
    override fun displayComponent(
        scope: Any?
    ) {
        val textStyle = LocalTextStyle.current.let { current ->
            current.copy(
                color = fontColor.value ?: current.color,
                fontSize = fontSize.value ?: current.fontSize,
            )
        }
        Text(
            text = textValue.value ?: "",
            style = textStyle
        )
    }
}
