package com.tezov.tuucho.core.presentation.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.tezov.tuucho.core.domain.business.jsonSchema.material._element.layout.LayoutLinearSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material._element.layout.LayoutLinearSchema.Style
import com.tezov.tuucho.core.domain.business.jsonSchema.material._element.layout.LayoutLinearSchema.Style.Value.Orientation
import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import com.tezov.tuucho.core.presentation.tool.modifier.then
import com.tezov.tuucho.core.presentation.tool.modifier.thenOnNotNull
import com.tezov.tuucho.core.presentation.ui._system.subset
import com.tezov.tuucho.core.presentation.ui.render.projectable.color
import com.tezov.tuucho.core.presentation.ui.render.projectable.dimension
import com.tezov.tuucho.core.presentation.ui.render.projectable.projection
import com.tezov.tuucho.core.presentation.ui.render.projectable.view
import com.tezov.tuucho.core.presentation.ui.render.projection.BooleanProjectionProtocol
import com.tezov.tuucho.core.presentation.ui.render.projection.ColorProjectionProtocol
import com.tezov.tuucho.core.presentation.ui.render.projection.StringProjectionProtocol
import com.tezov.tuucho.core.presentation.ui.render.projection.ViewsProjectionProtocol
import com.tezov.tuucho.core.presentation.ui.render.projector.componentProjector
import com.tezov.tuucho.core.presentation.ui.render.projector.content
import com.tezov.tuucho.core.presentation.ui.render.projector.style
import com.tezov.tuucho.core.presentation.ui.screen.Screen
import com.tezov.tuucho.core.presentation.ui.view._system.ViewFactoryProtocol
import kotlinx.serialization.json.JsonObject

class LayoutLinearViewFactory : ViewFactoryProtocol {
    override fun accept(
        componentObject: JsonObject
    ) = componentObject.subset == LayoutLinearSchema.Component.Value.subset

    override suspend fun process(
        screen: Screen,
        path: JsonElementPath,
    ) = LayoutLinearView(
        screen = screen,
        path = path,
    ).also { it.init() }
}

class LayoutLinearView(
    screen: Screen,
    path: JsonElementPath,
) : AbstractView(screen, path) {

    private lateinit var backgroundColor: ColorProjectionProtocol
    private lateinit var orientation: StringProjectionProtocol
    private lateinit var fillMaxSize: BooleanProjectionProtocol
    private lateinit var fillMaxWidth: BooleanProjectionProtocol
    private lateinit var itemViews: ViewsProjectionProtocol

    override suspend fun createComponentProjectorProjection() = componentProjector {
        style {
            color {
                backgroundColor = projection(Style.Key.backgroundColor)
            }
            dimension {
                fillMaxSize = projection(Style.Key.fillMaxSize)
                fillMaxWidth = projection(Style.Key.fillMaxWidth)
                orientation = projection(Style.Key.orientation)
            }
        }
        content {
            view {
                itemViews = projection(
                    key = LayoutLinearSchema.Content.Key.items,
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
        val modifier = Modifier
            .then {
                ifTrue(fillMaxSize.value) {
                    fillMaxSize()
                } or ifTrue(fillMaxWidth.value) {
                    fillMaxWidth()
                }
            }.thenOnNotNull(backgroundColor.value) { background(it) }

        when (orientation.value) {
            Orientation.horizontal -> {
                Row(modifier = modifier) {
                    itemViews.value?.forEach { it.display(this@Row) }
                }
            }

            else -> {
                Column(modifier = modifier) {
                    itemViews.value?.forEach { it.display(this@Column) }
                }
            }
        }
    }
}
