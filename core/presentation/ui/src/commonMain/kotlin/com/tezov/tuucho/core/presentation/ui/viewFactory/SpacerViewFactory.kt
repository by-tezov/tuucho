package com.tezov.tuucho.core.presentation.ui.viewFactory

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tezov.tuucho.core.domain.model.schema._system.withScope
import com.tezov.tuucho.core.domain.model.schema.material.ComponentSchema
import com.tezov.tuucho.core.domain.model.schema.material.DimensionSchema
import com.tezov.tuucho.core.domain.model.schema.material.StyleSchema
import com.tezov.tuucho.core.domain.model.schema.material.SubsetSchema
import com.tezov.tuucho.core.domain.model.schema.material.TypeSchema
import com.tezov.tuucho.core.domain.model.schema.material._element.SpacerSchema
import com.tezov.tuucho.core.presentation.ui.viewFactory._system.View
import com.tezov.tuucho.core.presentation.ui.viewFactory._system.ViewFactory
import kotlinx.serialization.json.JsonObject

class SpacerViewFactory : ViewFactory() {

    override fun accept(componentElement: JsonObject) = componentElement.let {
        it.withScope(TypeSchema::Scope).self == TypeSchema.Value.component &&
                it.withScope(SubsetSchema::Scope).self == SpacerSchema.Component.Value.subset
    }

    override fun process(url: String, componentObject: JsonObject) =
        SpacerView(url, componentObject)
            .also { it.init() }
}

class SpacerView(
    url: String,
    componentElement: JsonObject
) : View(url, componentElement) {

    private var _width: JsonObject? = null
    private var _height: JsonObject? = null
    private var _weight: JsonObject? = null

    override fun JsonObject.processComponent() {
        withScope(ComponentSchema::Scope).run {
            style?.processStyle()
        }
    }

    override fun JsonObject.processStyle() {
        withScope(SpacerSchema.Style::Scope).run {
            width?.processDimension(StyleSchema.Key.width)
            height?.processDimension(StyleSchema.Key.height)
            weight?.processDimension(SpacerSchema.Style.Key.weight)
        }
    }

    override fun JsonObject.processDimension(key: String) {
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

        //TODO do much better than that
        var modifier: Modifier = Modifier
        weight?.let {
            scope.apply {
                when (this) {
                    is ColumnScope -> modifier = modifier.weight(it)
                    is RowScope -> modifier = modifier.weight(it)
                }
            }
        } ?: run {
            width?.let { modifier = modifier.width(it) }
            height?.let { modifier = modifier.height(it) }
        }
        Spacer(
            modifier = modifier
                .background(color = Color.Gray)
        )
    }

}