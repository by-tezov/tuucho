package com.tezov.tuucho.core.presentation.ui.view

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import com.tezov.tuucho.core.domain.business.jsonSchema.material._element.LabelSchema.Component
import com.tezov.tuucho.core.domain.business.jsonSchema.material._element.LabelSchema.Content
import com.tezov.tuucho.core.domain.business.jsonSchema.material._element.LabelSchema.Style
import com.tezov.tuucho.core.domain.tool.extension.ExtensionBoolean.isTrueOrNull
import com.tezov.tuucho.core.presentation.ui._system.subset
import com.tezov.tuucho.core.presentation.ui.render.projection.ColorProjectionProtocol
import com.tezov.tuucho.core.presentation.ui.render.projection.TextProjectionProtocol
import com.tezov.tuucho.core.presentation.ui.render.projection.color
import com.tezov.tuucho.core.presentation.ui.render.projection.contextual
import com.tezov.tuucho.core.presentation.ui.render.projection.dimension.SpProjectionProtocol
import com.tezov.tuucho.core.presentation.ui.render.projection.dimension.mutable
import com.tezov.tuucho.core.presentation.ui.render.projection.dimension.sp
import com.tezov.tuucho.core.presentation.ui.render.projection.mutable
import com.tezov.tuucho.core.presentation.ui.render.projection.text
import com.tezov.tuucho.core.presentation.ui.render.projector.componentProjector
import com.tezov.tuucho.core.presentation.ui.render.projector.content
import com.tezov.tuucho.core.presentation.ui.render.projector.contextual
import com.tezov.tuucho.core.presentation.ui.render.projector.style
import com.tezov.tuucho.core.presentation.ui.screen.protocol.ScreenContextProtocol
import com.tezov.tuucho.core.presentation.ui.view.protocol.ViewFactoryProtocol
import com.tezov.tuucho.core.presentation.ui.view.protocol.ViewProtocol
import kotlinx.serialization.json.JsonObject

interface LabelViewProtocol : ViewProtocol {
    @Composable
    fun ComposeComponent(
        textValue: String?,
        fontColor: Color?,
        fontSize: TextUnit?,
    )

    @Composable
    fun ComposePlaceHolder()
}

fun createLabelView(
    screenContext: ScreenContextProtocol,
): LabelViewProtocol = LabelView(
    screenContext = screenContext
)

class LabelViewFactory : ViewFactoryProtocol {
    override fun accept(
        componentObject: JsonObject
    ) = componentObject.subset == Component.Value.subset

    override suspend fun process(
        screenContext: ScreenContextProtocol,
    ): LabelViewProtocol = createLabelView(
        screenContext = screenContext,
    )
}

private class LabelView(
    screenContext: ScreenContextProtocol,
) : LabelViewProtocol, AbstractView(screenContext) {
    private lateinit var textValue: TextProjectionProtocol
    private lateinit var fontColor: ColorProjectionProtocol
    private lateinit var fontSize: SpProjectionProtocol

    override suspend fun createComponentProjector() = componentProjector {
        +style {
            fontColor = +color(Style.Key.fontColor).mutable
            fontSize = +sp(Style.Key.fontSize).mutable
        }.contextual
        +content {
            textValue = +text(Content.Key.value).mutable.contextual
        }.contextual
    }.contextual

    override fun getResolvedStatus() = textValue.hasBeenResolved.isTrueOrNull &&
        fontColor.hasBeenResolved.isTrueOrNull &&
        fontSize.hasBeenResolved.isTrueOrNull

    @Composable
    override fun displayComponent(
        scope: Any?
    ) {
        ComposeComponent(
            textValue = textValue.value,
            fontColor = fontColor.value,
            fontSize = fontSize.value
        )
    }

    @Composable
    override fun ComposeComponent(
        textValue: String?,
        fontColor: Color?,
        fontSize: TextUnit?,
    ) {
        val textStyle = LocalTextStyle.current.let { current ->
            current.copy(
                color = fontColor ?: current.color,
                fontSize = fontSize ?: current.fontSize,
            )
        }
        Text(
            text = textValue ?: "",
            style = textStyle
        )
    }

    @Composable
    override fun ComposePlaceHolder() {
        displayPlaceholder(null)
    }
}
