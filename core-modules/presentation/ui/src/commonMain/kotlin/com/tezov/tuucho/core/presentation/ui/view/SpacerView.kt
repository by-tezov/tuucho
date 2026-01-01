package com.tezov.tuucho.core.presentation.ui.view

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.tezov.tuucho.core.domain.business.jsonSchema.material.StyleSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material._element.SpacerSchema.Component
import com.tezov.tuucho.core.domain.business.jsonSchema.material._element.SpacerSchema.Style
import com.tezov.tuucho.core.domain.tool.extension.ExtensionBoolean.isTrueOrNull
import com.tezov.tuucho.core.presentation.tool.modifier.then
import com.tezov.tuucho.core.presentation.ui._system.subset
import com.tezov.tuucho.core.presentation.ui.render.projection.dimension.DpProjectionProtocol
import com.tezov.tuucho.core.presentation.ui.render.projection.dimension.FloatProjectionProtocol
import com.tezov.tuucho.core.presentation.ui.render.projection.dimension.dp
import com.tezov.tuucho.core.presentation.ui.render.projection.dimension.float
import com.tezov.tuucho.core.presentation.ui.render.projector.componentProjector
import com.tezov.tuucho.core.presentation.ui.render.projector.style
import com.tezov.tuucho.core.presentation.ui.screen.dummyScreenContext
import com.tezov.tuucho.core.presentation.ui.screen.protocol.ScreenContextProtocol
import com.tezov.tuucho.core.presentation.ui.view.protocol.ViewFactoryProtocol
import com.tezov.tuucho.core.presentation.ui.view.protocol.ViewProtocol
import kotlinx.serialization.json.JsonObject

interface SpacerViewProtocol : ViewProtocol {
    @Composable
    fun ComposeRowComponent(
        scope: RowScope,
        weight: Float
    )

    @Composable
    fun ComposeColumnComponent(
        scope: ColumnScope,
        weight: Float
    )

    @Composable
    fun ComposeDefaultComponent(
        width: Dp?,
        height: Dp?,
    )

    @Composable
    fun ComposePlaceHolder()
}

fun createSpacerView(
    screenContext: ScreenContextProtocol = dummyScreenContext(),
): SpacerViewProtocol = SpacerView(
    screenContext = screenContext
)

class SpacerViewFactory : ViewFactoryProtocol {
    override fun accept(
        componentObject: JsonObject
    ) = componentObject.subset == Component.Value.subset

    override suspend fun process(
        screenContext: ScreenContextProtocol,
    ): SpacerViewProtocol = createSpacerView(
        screenContext = screenContext,
    )
}

private class SpacerView(
    screenContext: ScreenContextProtocol,
) : SpacerViewProtocol, AbstractView(screenContext) {
    private lateinit var width: DpProjectionProtocol
    private lateinit var height: DpProjectionProtocol
    private lateinit var weight: FloatProjectionProtocol

    override suspend fun createComponentProjector() = componentProjector {
        +style {
            width = +dp(StyleSchema.Key.width)
            height = +dp(StyleSchema.Key.height)
            weight = +float(Style.Key.weight)
        }
    }

    override fun getResolvedStatus() = width.hasBeenResolved.isTrueOrNull &&
        height.hasBeenResolved.isTrueOrNull &&
        weight.hasBeenResolved.isTrueOrNull

    @Composable
    override fun displayComponent(
        scope: Any?
    ) {
        weight.value?.let {
            when (scope) {
                is ColumnScope -> ComposeColumnComponent(scope, it)
                is RowScope -> ComposeRowComponent(scope, it)
                else -> ComposeDefaultComponent(width.value, height.value)
            }
        } ?: ComposeDefaultComponent(width.value, height.value)
    }

    @Composable
    override fun ComposeRowComponent(
        scope: RowScope,
        weight: Float
    ) {
        Spacer(modifier = scope.run { Modifier.weight(weight) })
    }

    @Composable
    override fun ComposeColumnComponent(
        scope: ColumnScope,
        weight: Float
    ) {
        Spacer(modifier = scope.run { Modifier.weight(weight) })
    }

    @Composable
    override fun ComposeDefaultComponent(
        width: Dp?,
        height: Dp?,
    ) {
        Spacer(modifier = Modifier.then {
            ifNotNull(width) { width(it) }
            ifNotNull(height) { height(it) }
        })
    }

    @Composable
    override fun ComposePlaceHolder() {
        displayPlaceholder(null)
    }
}
