package com.tezov.tuucho.sample.uiExtension.presentation

import androidx.compose.foundation.clickable
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.IdSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.domain.tool.extension.ExtensionBoolean.isTrueOrNull
import com.tezov.tuucho.core.presentation.ui._system.subset
import com.tezov.tuucho.core.presentation.ui.render.projection.ActionProjectionProtocol
import com.tezov.tuucho.core.presentation.ui.render.projection.ColorProjectionProtocol
import com.tezov.tuucho.core.presentation.ui.render.projection.TextProjectionProtocol
import com.tezov.tuucho.core.presentation.ui.render.projection.action
import com.tezov.tuucho.core.presentation.ui.render.projection.color
import com.tezov.tuucho.core.presentation.ui.render.projection.contextual
import com.tezov.tuucho.core.presentation.ui.render.projection.dimension.SpProjectionProtocol
import com.tezov.tuucho.core.presentation.ui.render.projection.dimension.sp
import com.tezov.tuucho.core.presentation.ui.render.projection.message.MessageIntProjectionProtocol
import com.tezov.tuucho.core.presentation.ui.render.projection.message.int
import com.tezov.tuucho.core.presentation.ui.render.projection.message.mutable
import com.tezov.tuucho.core.presentation.ui.render.projection.mutable
import com.tezov.tuucho.core.presentation.ui.render.projection.text
import com.tezov.tuucho.core.presentation.ui.render.projector.componentProjector
import com.tezov.tuucho.core.presentation.ui.render.projector.content
import com.tezov.tuucho.core.presentation.ui.render.projector.contextual
import com.tezov.tuucho.core.presentation.ui.render.projector.message
import com.tezov.tuucho.core.presentation.ui.render.projector.style
import com.tezov.tuucho.core.presentation.ui.screen.protocol.ScreenContextProtocol
import com.tezov.tuucho.core.presentation.ui.view.AbstractView
import com.tezov.tuucho.core.presentation.ui.view.protocol.ViewFactoryProtocol
import com.tezov.tuucho.core.presentation.ui.view.protocol.ViewProtocol
import com.tezov.tuucho.sample.uiExtension.domain.CustomLabelSchema.Component
import com.tezov.tuucho.sample.uiExtension.domain.CustomLabelSchema.Content
import com.tezov.tuucho.sample.uiExtension.domain.CustomLabelSchema.Message
import com.tezov.tuucho.sample.uiExtension.domain.CustomLabelSchema.Style
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject

interface CustomLabelViewProtocol : ViewProtocol {
    @Composable
    fun ComposeComponent(
        onClick: () -> Unit,
        textValue: String?,
        fontColor: Color?,
        fontSize: TextUnit?,
        counter: Int
    )

    @Composable
    fun ComposePlaceHolder()
}

class CustomLabelViewFactory : ViewFactoryProtocol {

    companion object {
        fun createCustomLabelView(
            screenContext: ScreenContextProtocol,
        ): CustomLabelViewProtocol = CustomLabelView(
            screenContext = screenContext
        )
    }

    override fun accept(
        componentObject: JsonObject
    ) = componentObject.subset == Component.Value.subset

    override suspend fun process(
        screenContext: ScreenContextProtocol,
    ): CustomLabelViewProtocol = createCustomLabelView(
        screenContext = screenContext,
    )
}

private class CustomLabelView(
    screenContext: ScreenContextProtocol,
) : AbstractView(screenContext),
    CustomLabelViewProtocol {

    private var fontColor = mutableStateOf(Color.LightGray)
    private lateinit var messageId: Lazy<String?>
    private lateinit var counter: MessageIntProjectionProtocol
    private lateinit var textValue: TextProjectionProtocol
    private lateinit var fontColorLight: ColorProjectionProtocol
    private lateinit var fontColorDark: ColorProjectionProtocol
    private lateinit var fontSize: SpProjectionProtocol
    private lateinit var action: ActionProjectionProtocol

    override suspend fun createComponentProjector() = componentProjector {
        +style {
            fontColorLight = +color(Style.Key.fontColorLight)
            fontColorDark = +color(Style.Key.fontColorDark)
            fontSize = +sp(Style.Key.fontSize)
        }
        +content {
            action = +action(
                key = Content.Key.action,
                route = screenContext.route
            )
            textValue = +text(Content.Key.value).mutable.contextual
        }
        +message(
            Message.Value.Subset.customLabelMessage,
            onReceived = ::onReceivedMessage,
        ) {
            counter = +int(Message.Key.downstream).mutable.apply { value = 0 }
        }.also { messageId = it.lazyId }
    }.contextual

    override suspend fun initialized() {
        super.initialized()
        onReceivedMessage()
    }

    private fun onReceivedMessage() {
        fontColor.value = when (fontColor.value) {
            fontColorLight.value -> fontColorDark.value
            else -> fontColorLight.value
        } ?: Color.LightGray
    }

    override fun getResolvedStatus() = textValue.hasBeenResolved.isTrueOrNull &&
            action.hasBeenResolved.isTrueOrNull &&
            fontColorLight.hasBeenResolved.isTrueOrNull &&
            fontColorDark.hasBeenResolved.isTrueOrNull &&
            fontSize.hasBeenResolved.isTrueOrNull

    private fun onClick() {
        action.value?.let {
            val idObject = JsonNull
                .withScope(IdSchema::Scope)
                .apply {
                    value = messageId.value
                }.collect()
            val message = JsonNull
                .withScope(Message::Scope)
                .apply {
                    id = idObject
                    type = TypeSchema.Value.message
                    subset = Message.Value.Subset.customLabelMessage
                    upstream = counter.value
                }.collect()
            it.invoke(message)
        }
    }

    @Composable
    override fun displayComponent(
        scope: Any?
    ) {
        ComposeComponent(
            onClick = ::onClick,
            textValue = textValue.value,
            fontColor = fontColor.value,
            fontSize = fontSize.value,
            counter = counter.value ?: -1
        )
    }

    @Composable
    override fun ComposeComponent(
        onClick: () -> Unit,
        textValue: String?,
        fontColor: Color?,
        fontSize: TextUnit?,
        counter: Int
    ) {
        val textStyle = LocalTextStyle.current.let { current ->
            current.copy(
                color = fontColor ?: current.color,
                fontSize = fontSize ?: current.fontSize,
            )
        }
        Text(
            modifier = Modifier.clickable(onClick = onClick),
            text = buildString {
                textValue?.let {
                    append(it)
                    append(": $counter")
                } ?: append(":: $counter")
            },
            style = textStyle
        )
    }

    @Composable
    override fun ComposePlaceHolder() {
        displayPlaceholder(null)
    }
}
