package com.tezov.tuucho.uiComponent.stable.presentation.view

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.tezov.tuucho.core.domain.tool.extension.ExtensionBoolean.isTrueOrNull
import com.tezov.tuucho.core.presentation.tool.modifier.then
import com.tezov.tuucho.core.presentation.ui._system.subset
import com.tezov.tuucho.core.presentation.ui.render.projection.dimension.DpProjectionProtocol
import com.tezov.tuucho.core.presentation.ui.render.projection.dimension.FloatProjectionProtocol
import com.tezov.tuucho.core.presentation.ui.render.projection.dimension.dp
import com.tezov.tuucho.core.presentation.ui.render.projection.dimension.float
import com.tezov.tuucho.core.presentation.ui.render.projector.componentProjector
import com.tezov.tuucho.core.presentation.ui.render.projector.style
import com.tezov.tuucho.core.presentation.ui.screen.protocol.ScreenContextProtocol
import com.tezov.tuucho.core.presentation.ui.view.AbstractView
import com.tezov.tuucho.core.presentation.ui.view.protocol.ViewFactoryProtocol
import com.tezov.tuucho.core.presentation.ui.view.protocol.ViewProtocol
import com.tezov.tuucho.uiComponent.stable.domain.jsonSchema.material.SpacerSchema.Component
import com.tezov.tuucho.uiComponent.stable.domain.jsonSchema.material.SpacerSchema.Style
import kotlinx.serialization.json.JsonObject

interface SpacerViewProtocol : ViewProtocol {
    @Composable
    fun ComposeComponent(
        scope: Any?,
        weight: Float?,
        width: Dp?,
        height: Dp?,
    )

    @Composable
    fun ComposePlaceHolder()
}

class SpacerViewFactory : ViewFactoryProtocol {
    companion object {
        fun createSpacerView(
            screenContext: ScreenContextProtocol,
        ): SpacerViewProtocol = SpacerView(
            screenContext = screenContext
        )
    }

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
) : AbstractView(screenContext),
    SpacerViewProtocol {
    private lateinit var width: DpProjectionProtocol
    private lateinit var height: DpProjectionProtocol
    private lateinit var weight: FloatProjectionProtocol

    override suspend fun createComponentProjector() = componentProjector {
        +style {
            width = +dp(Style.Key.width)
            height = +dp(Style.Key.height)
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
        ComposeComponent(
            scope = scope,
            weight = weight.value,
            width = width.value,
            height = height.value
        )
    }

    @Composable
    override fun ComposeComponent(
        scope: Any?,
        weight: Float?,
        width: Dp?,
        height: Dp?,
    ) {
        Spacer(
            modifier = Modifier
                .then {
                    ifNotNull(weight) { weight ->
                        when (scope) {
                            is ColumnScope -> scope.run { weight(weight) }
                            is RowScope -> scope.run { weight(weight) }
                            else -> this
                        }
                    } or {
                        ifNotNull(width) { width(it) }
                        ifNotNull(height) { height(it) }
                    }
                }
        )
    }

    @Composable
    override fun ComposePlaceHolder() {
        displayPlaceholder(null)
    }
}
