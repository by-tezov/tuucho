package com.tezov.tuucho.core.presentation.ui.view

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.tezov.tuucho.core.domain.business.jsonSchema.material.StyleSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material._element.SpacerSchema
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
import com.tezov.tuucho.core.presentation.ui.screen.ScreenContextProtocol
import com.tezov.tuucho.core.presentation.ui.view._system.ViewFactoryProtocol
import kotlinx.serialization.json.JsonObject

class SpacerViewFactory : ViewFactoryProtocol {
    override fun accept(
        componentObject: JsonObject
    ) = componentObject.subset == SpacerSchema.Component.Value.subset

    override suspend fun process(
        screenContext: ScreenContextProtocol,
    ) = SpacerView(
        screenContext = screenContext,
    )
}

class SpacerView(
    screenContext: ScreenContextProtocol,
) : AbstractView(screenContext) {
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
        Spacer(
            modifier = Modifier
                .then {
                    ifNotNull(weight.value) { weight ->
                        when (scope) {
                            is ColumnScope -> scope.run { weight(weight) }
                            is RowScope -> scope.run { weight(weight) }
                            else -> this
                        }
                    } or {
                        ifNotNull(width.value) { width(it) }
                        ifNotNull(height.value) { height(it) }
                    }
                }
        )
    }
}
