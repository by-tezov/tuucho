package com.tezov.tuucho.core.presentation.ui.renderer.view

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.ComponentSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.DimensionSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.StyleSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.SubsetSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material.TypeSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.material._element.SpacerSchema
import com.tezov.tuucho.core.presentation.tool.modifier.onNotNull
import com.tezov.tuucho.core.presentation.tool.modifier.then
import com.tezov.tuucho.core.presentation.ui.renderer.view._system.AbstractViewFactory
import kotlinx.serialization.json.JsonObject

class SpacerViewFactory : AbstractViewFactory() {

    override fun accept(componentElement: JsonObject) = componentElement.let {
        it.withScope(TypeSchema::Scope).self == TypeSchema.Value.component &&
                it.withScope(SubsetSchema::Scope).self == SpacerSchema.Component.Value.subset
    }

    override suspend fun process(
        route: NavigationRoute.Url,
        componentObject: JsonObject,
    ) =
        SpacerView(
            componentObject = componentObject
        ).also { it.init() }
}

class SpacerView(
    componentObject: JsonObject,
) : AbstractView(componentObject) {

    private var _width: JsonObject? = null
    private var _height: JsonObject? = null
    private var _weight: JsonObject? = null

    override suspend fun JsonObject.processComponent() {
        withScope(ComponentSchema::Scope).run {
            style?.processStyle()
        }
    }

    override suspend fun JsonObject.processStyle() {
        withScope(SpacerSchema.Style::Scope).run {
            width?.processDimension(StyleSchema.Key.width)
            height?.processDimension(StyleSchema.Key.height)
            weight?.processDimension(SpacerSchema.Style.Key.weight)
        }
    }

    override suspend fun JsonObject.processDimension(key: String) {
        when (key) {
            StyleSchema.Key.width -> _width = this
            StyleSchema.Key.height -> _height = this
            SpacerSchema.Style.Key.weight -> _weight = this
        }
    }

    private val width
        get():Dp? {
            return _width?.withScope(DimensionSchema::Scope)
                ?.default?.toIntOrNull()?.dp
        }

    private val height
        get():Dp? {
            return _height?.withScope(DimensionSchema::Scope)
                ?.default?.toIntOrNull()?.dp
        }

    private val weight
        get():Float? {
            return _weight?.withScope(DimensionSchema::Scope)
                ?.default?.toFloatOrNull()
        }

    @Composable
    override fun displayComponent(scope: Any?) {
        Spacer(
            modifier = Modifier
                .then {
                    (onNotNull(weight) { weight ->
                        when (scope) {
                            is ColumnScope -> with(scope) { weight(weight) }
                            is RowScope -> with(scope) { weight(weight) }
                            else -> null
                        } ?: this
                    } or {
                        onNotNull(width) { width(it) }
                        onNotNull(height) { height(it) }
                    })
                }
        )
    }

}