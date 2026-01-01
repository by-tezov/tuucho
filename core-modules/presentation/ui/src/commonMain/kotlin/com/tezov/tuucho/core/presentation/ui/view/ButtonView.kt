package com.tezov.tuucho.core.presentation.ui.view

import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import com.tezov.tuucho.core.domain.business.jsonSchema.material._element.ButtonSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material._element.ButtonSchema.Content
import com.tezov.tuucho.core.presentation.ui._system.subset
import com.tezov.tuucho.core.presentation.ui.render.projection.ActionProjectionProtocol
import com.tezov.tuucho.core.presentation.ui.render.projection.action
import com.tezov.tuucho.core.presentation.ui.render.projection.contextual
import com.tezov.tuucho.core.presentation.ui.render.projection.mutable
import com.tezov.tuucho.core.presentation.ui.render.projection.view.ViewProjectionProtocol
import com.tezov.tuucho.core.presentation.ui.render.projection.view.view
import com.tezov.tuucho.core.presentation.ui.render.projector.componentProjector
import com.tezov.tuucho.core.presentation.ui.render.projector.content
import com.tezov.tuucho.core.presentation.ui.render.projector.contextual
import com.tezov.tuucho.core.presentation.ui.screen.ScreenContextProtocol
import com.tezov.tuucho.core.presentation.ui.view._system.ViewFactoryProtocol
import kotlinx.serialization.json.JsonObject

class ButtonViewFactory : ViewFactoryProtocol {
    override fun accept(
        componentObject: JsonObject,
    ) = componentObject.subset == ButtonSchema.Component.Value.subset

    override suspend fun process(
        screenContext: ScreenContextProtocol,
    ) = ButtonView(
        screenContext = screenContext,
    )
}

class ButtonView(
    screenContext: ScreenContextProtocol,
) : AbstractView(screenContext) {
    private lateinit var labelView: ViewProjectionProtocol
    private lateinit var action: ActionProjectionProtocol

    override suspend fun createComponentProjector() = componentProjector {
        +content {
            action = +action(
                key = Content.Key.action,
                route = screenContext.route
            ).mutable.contextual
            labelView = +view(
                key = Content.Key.label,
                screenContext = screenContext
            ) // TODO could be contextual ?
        }.contextual
    }.contextual

    override fun getResolvedStatus() = true

    @Composable
    override fun displayComponent(
        scope: Any?
    ) {
        Button(
            onClick = action.value ?: {},
            content = { labelView.value?.display(this) }
        )
    }
}
