package com.tezov.tuucho.core.presentation.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.tezov.tuucho.core.domain.business.jsonSchema.material._element.layout.LayoutLinearSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material._element.layout.LayoutLinearSchema.Style
import com.tezov.tuucho.core.domain.business.jsonSchema.material._element.layout.LayoutLinearSchema.Style.Value.Orientation
import com.tezov.tuucho.core.domain.tool.extension.ExtensionBoolean.isTrueOrNull
import com.tezov.tuucho.core.presentation.tool.modifier.then
import com.tezov.tuucho.core.presentation.tool.modifier.thenIfNotNull
import com.tezov.tuucho.core.presentation.ui._system.subset
import com.tezov.tuucho.core.presentation.ui.render.projection.ColorProjectionProtocol
import com.tezov.tuucho.core.presentation.ui.render.projection.color
import com.tezov.tuucho.core.presentation.ui.render.projection.dimension.BooleanProjectionProtocol
import com.tezov.tuucho.core.presentation.ui.render.projection.dimension.StringProjectionProtocol
import com.tezov.tuucho.core.presentation.ui.render.projection.dimension.boolean
import com.tezov.tuucho.core.presentation.ui.render.projection.dimension.mutable
import com.tezov.tuucho.core.presentation.ui.render.projection.dimension.string
import com.tezov.tuucho.core.presentation.ui.render.projection.mutable
import com.tezov.tuucho.core.presentation.ui.render.projection.view.ViewsProjectionProtocol
import com.tezov.tuucho.core.presentation.ui.render.projection.view.views
import com.tezov.tuucho.core.presentation.ui.render.projector.componentProjector
import com.tezov.tuucho.core.presentation.ui.render.projector.content
import com.tezov.tuucho.core.presentation.ui.render.projector.contextual
import com.tezov.tuucho.core.presentation.ui.render.projector.style
import com.tezov.tuucho.core.presentation.ui.screen.protocol.ScreenContextProtocol
import com.tezov.tuucho.core.presentation.ui.view.protocol.ViewFactoryProtocol
import com.tezov.tuucho.core.presentation.ui.view.protocol.ViewProtocol
import kotlinx.serialization.json.JsonObject

interface LayoutLinearViewProtocol : ViewProtocol {
    @Composable
    fun ComposeComponent(
        backgroundColor: Color?,
        orientation: String?,
        fillMaxSize: Boolean?,
        fillMaxWidth: Boolean?,
        contents: List<@Composable Any.() -> Unit>,
    )

    @Composable
    fun ComposePlaceHolder()
}

fun createLayoutLinearView(
    screenContext: ScreenContextProtocol,
): LayoutLinearViewProtocol = LayoutLinearView(
    screenContext = screenContext
)

class LayoutLinearViewFactory : ViewFactoryProtocol {
    override fun accept(
        componentObject: JsonObject
    ) = componentObject.subset == LayoutLinearSchema.Component.Value.subset

    override suspend fun process(
        screenContext: ScreenContextProtocol,
    ): LayoutLinearViewProtocol = LayoutLinearView(
        screenContext = screenContext,
    )
}

private class LayoutLinearView(
    screenContext: ScreenContextProtocol,
) : LayoutLinearViewProtocol, AbstractView(screenContext) {
    private lateinit var backgroundColor: ColorProjectionProtocol
    private lateinit var orientation: StringProjectionProtocol
    private lateinit var fillMaxSize: BooleanProjectionProtocol
    private lateinit var fillMaxWidth: BooleanProjectionProtocol
    private lateinit var itemViews: ViewsProjectionProtocol

    override suspend fun createComponentProjector() = componentProjector {
        +style {
            backgroundColor = +color(Style.Key.backgroundColor).mutable
            orientation = +string(Style.Key.orientation).mutable
            fillMaxSize = +boolean(Style.Key.fillMaxSize).mutable
            fillMaxWidth = +boolean(Style.Key.fillMaxWidth).mutable
        }.contextual
        +content {
            itemViews = +views(
                LayoutLinearSchema.Content.Key.items,
                screenContext = screenContext
            )
        }.contextual
    }.contextual

    override fun getResolvedStatus() = itemViews.hasBeenResolved.isTrueOrNull

    @Composable
    override fun displayComponent(
        scope: Any?
    ) {
        ComposeComponent(
            backgroundColor = backgroundColor.value,
            orientation = orientation.value,
            fillMaxSize = fillMaxSize.value,
            fillMaxWidth = fillMaxWidth.value,
            contents = buildList {
                itemViews.views.let { views ->
                    for (view in views) {
                        add({ view.value?.display(this) })
                    }
                }
            }
        )
    }

    @Composable
    override fun ComposeComponent(
        backgroundColor: Color?,
        orientation: String?,
        fillMaxSize: Boolean?,
        fillMaxWidth: Boolean?,
        contents: List<@Composable Any.() -> Unit>,
    ) {
        val modifier = Modifier
            .then {
                ifTrue(fillMaxSize) {
                    fillMaxSize()
                } or ifTrue(fillMaxWidth) {
                    fillMaxWidth()
                }
            }
            .thenIfNotNull(backgroundColor) { background(it) }
        when (orientation) {
            Orientation.horizontal -> Row(modifier = modifier) {
                contents.forEach { it.invoke(this) }
            }

            else -> Column(modifier = modifier) {
                contents.forEach { it.invoke(this) }
            }
        }
    }

    @Composable
    override fun ComposePlaceHolder() {
        displayPlaceholder(null)
    }
}
