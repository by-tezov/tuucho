package com.tezov.tuucho.core.presentation.ui.view

import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import com.tezov.tuucho.core.domain.business.jsonSchema.material._element.ButtonSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material._element.ButtonSchema.Content
import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import com.tezov.tuucho.core.presentation.ui._system.subset
import com.tezov.tuucho.core.presentation.ui.render.projectable.action
import com.tezov.tuucho.core.presentation.ui.render.projectable.projection
import com.tezov.tuucho.core.presentation.ui.render.projectable.view
import com.tezov.tuucho.core.presentation.ui.render.projection.ActionProjectionProtocol
import com.tezov.tuucho.core.presentation.ui.render.projection.ViewProjectionProtocol
import com.tezov.tuucho.core.presentation.ui.render.projector.componentProjector
import com.tezov.tuucho.core.presentation.ui.render.projector.content
import com.tezov.tuucho.core.presentation.ui.screen.Screen
import com.tezov.tuucho.core.presentation.ui.view._system.ViewFactoryProtocol
import kotlinx.serialization.json.JsonObject

class ButtonViewFactory : ViewFactoryProtocol {
    override fun accept(
        componentObject: JsonObject,
    ) = componentObject.subset == ButtonSchema.Component.Value.subset

    override suspend fun process(
        screen: Screen,
        path: JsonElementPath,
    ) = ButtonView(
        screen = screen,
        path = path,
    ).also { it.init() }
}

class ButtonView(
    screen: Screen,
    path: JsonElementPath,
) : AbstractView(screen, path) {

    private lateinit var labelView: ViewProjectionProtocol
    private lateinit var action: ActionProjectionProtocol

    override suspend fun createComponentProjectorProjection() = componentProjector {
        content {
            action {
                action = projection(
                    key = Content.Key.action,
                    route = screen.route
                )
            }
            view {
                labelView = projection(
                    key = Content.Key.label,
                    screen = screen,
                    path = path.child(this@content.type)
                )
            }
        }
    }

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
