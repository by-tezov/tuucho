package com.tezov.tuucho.uiComponent.stable.presentation.view

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import com.tezov.tuucho.core.presentation.ui._system.subset
import com.tezov.tuucho.core.presentation.ui.render.projection.ActionProjectionProtocol
import com.tezov.tuucho.core.presentation.ui.render.projection.ActionTypeAlias
import com.tezov.tuucho.core.presentation.ui.render.projection.action
import com.tezov.tuucho.core.presentation.ui.render.projection.mutable
import com.tezov.tuucho.core.presentation.ui.render.projection.view.ViewProjectionProtocol
import com.tezov.tuucho.core.presentation.ui.render.projection.view.view
import com.tezov.tuucho.core.presentation.ui.render.projector.componentProjector
import com.tezov.tuucho.core.presentation.ui.render.projector.content
import com.tezov.tuucho.core.presentation.ui.render.projector.contextual
import com.tezov.tuucho.core.presentation.ui.screen.protocol.ScreenContextProtocol
import com.tezov.tuucho.core.presentation.ui.view.AbstractView
import com.tezov.tuucho.core.presentation.ui.view.protocol.ViewFactoryProtocol
import com.tezov.tuucho.core.presentation.ui.view.protocol.ViewProtocol
import com.tezov.tuucho.uiComponent.stable.domain.jsonSchema.material.ButtonSchema.Component
import com.tezov.tuucho.uiComponent.stable.domain.jsonSchema.material.ButtonSchema.Content
import kotlinx.serialization.json.JsonObject

interface ButtonViewProtocol : ViewProtocol {
    @Composable
    fun ComposeComponent(
        onClick: ActionTypeAlias?,
        content: @Composable RowScope.() -> Unit,
    )

    @Composable
    fun ComposePlaceHolder()
}

class ButtonViewFactory : ViewFactoryProtocol {
    companion object {
        fun createButtonView(
            screenContext: ScreenContextProtocol,
        ): ButtonViewProtocol = ButtonView(
            screenContext = screenContext
        )
    }

    override fun accept(
        componentObject: JsonObject,
    ) = componentObject.subset == Component.Value.subset

    override suspend fun process(
        screenContext: ScreenContextProtocol,
    ): ButtonViewProtocol = createButtonView(
        screenContext = screenContext,
    )
}

private class ButtonView(
    screenContext: ScreenContextProtocol,
) : AbstractView(screenContext),
    ButtonViewProtocol {
    private lateinit var labelView: ViewProjectionProtocol
    private lateinit var action: ActionProjectionProtocol

    override suspend fun createComponentProjector() = componentProjector {
        +content {
            action = +action(
                key = Content.Key.action,
                route = screenContext.route
            ).mutable
            labelView = +view(
                key = Content.Key.label,
                screenContext = screenContext
            ) // TODO could be contextual but it will clash with contextual added on componentProjector?
        }.contextual
    }.contextual

    override fun getResolvedStatus() = true

    @Composable
    override fun displayComponent(
        scope: Any?
    ) {
        ComposeComponent(
            onClick = action.value,
            content = { labelView.value?.display(this) }
        )
    }

    @Composable
    override fun ComposeComponent(
        onClick: ActionTypeAlias?,
        content: @Composable RowScope.() -> Unit,
    ) {
        Button(
            onClick = { onClick?.invoke(null) },
            content = content
        )
    }

    @Composable
    override fun ComposePlaceHolder() {
        displayPlaceholder(null)
    }
}
