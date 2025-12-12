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
import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import com.tezov.tuucho.core.presentation.tool.modifier.then
import com.tezov.tuucho.core.presentation.ui._system.subset
import com.tezov.tuucho.core.presentation.ui.render.projectable.dimension
import com.tezov.tuucho.core.presentation.ui.render.projectable.projection
import com.tezov.tuucho.core.presentation.ui.render.projection.DpProjection
import com.tezov.tuucho.core.presentation.ui.render.projection.FloatProjection
import com.tezov.tuucho.core.presentation.ui.render.projector.componentProjector
import com.tezov.tuucho.core.presentation.ui.render.projector.style
import com.tezov.tuucho.core.presentation.ui.render.protocol.ComponentProjectorProtocol
import com.tezov.tuucho.core.presentation.ui.screen.Screen
import com.tezov.tuucho.core.presentation.ui.view._system.ViewFactoryProtocol
import kotlinx.serialization.json.JsonObject

class SpacerViewFactory : ViewFactoryProtocol {
    override fun accept(
        componentObject: JsonObject
    ) = componentObject.subset == SpacerSchema.Component.Value.subset

    override suspend fun process(
        screen: Screen,
        path: JsonElementPath,
    ) = SpacerView(
        screen = screen,
        path = path,
    ).also { it.init() }
}

class SpacerView(
    screen: Screen,
    path: JsonElementPath,
) : AbstractView(screen, path) {

    override lateinit var componentProjector: ComponentProjectorProtocol

    private lateinit var width: DpProjection.Static
    private lateinit var height: DpProjection.Static
    private lateinit var weight: FloatProjection.Static

    override fun updateReadyStatus() {
        isReady = width.isReady.isTrueOrNull &&
            height.isReady.isTrueOrNull &&
            weight.isReady.isTrueOrNull
    }

    override suspend fun initProjection() {
        componentProjector = componentProjector {
            style {
                dimension {
                    width = projection(StyleSchema.Key.width)
                    height = projection(StyleSchema.Key.height)
                    weight = projection(Style.Key.weight)
                }
            }
        }
        componentProjector.process(componentObject)
    }

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
