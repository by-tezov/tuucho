package com.tezov.tuucho.core.ui.composable

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
import com.tezov.tuucho.core.domain.model.schema._system.onScope
import com.tezov.tuucho.core.domain.model.schema._system.withScope
import com.tezov.tuucho.core.domain.model.schema.material.ComponentSchema
import com.tezov.tuucho.core.domain.model.schema.material.DimensionSchema
import com.tezov.tuucho.core.domain.model.schema.material.IdSchema
import com.tezov.tuucho.core.domain.model.schema.material.SubsetSchema
import com.tezov.tuucho.core.domain.model.schema.material.TypeSchema
import com.tezov.tuucho.core.domain.model.schema.material._element.SpacerSchema
import com.tezov.tuucho.core.ui.composable._system.Screen
import com.tezov.tuucho.core.ui.composable._system.UiComponentFactory
import kotlinx.serialization.json.JsonObject

class SpacerRendered : UiComponentFactory() {

    override fun accept(componentElement: JsonObject) = componentElement.let {
            it.withScope(TypeSchema::Scope).self == TypeSchema.Value.component &&
            it.withScope(SubsetSchema::Scope).self == SpacerSchema.Component.Value.subset
        }

    override fun process(componentElement: JsonObject) = SpacerScreen(componentElement)
}

class SpacerScreen(
    componentElement: JsonObject
) : Screen() {

    private var _width: JsonObject? = null
    private var _height: JsonObject? = null
    private var _weight: JsonObject? = null

    init {
        val componentScope = componentElement.withScope(ComponentSchema::Scope)
        componentScope.onScope(IdSchema::Scope).value?.let {
            addProcessor(TypeSchema.Value.component, id = it, ::processComponent)
        }
        componentScope.style?.onScope(IdSchema::Scope)?.value?.let {
            addProcessor(TypeSchema.Value.style, id = it, ::processStyle)
        }
        processComponent(componentElement)
    }

    private fun processComponent(componentElement: JsonObject) {
        val componentScope = componentElement.withScope(ComponentSchema::Scope)
        componentScope.style?.let { processStyle(it) }
    }

    private fun processStyle(styleElement: JsonObject) {
        val styleScope = styleElement.withScope(SpacerSchema.Style::Scope)
        styleScope.width?.let { _width = it }
        styleScope.height?.let { _height = it }
        styleScope.weight?.let { _weight = it }
    }

    private val width
        @Composable get():Dp? {
            return _width?.withScope(DimensionSchema::Scope)
                ?.default?.toIntOrNull()?.dp
        }

    private val height
        @Composable get():Dp? {
            return _height?.withScope(DimensionSchema::Scope)
                ?.default?.toIntOrNull()?.dp
        }

    private val weight
        @Composable get():Float? {
            return _weight?.withScope(DimensionSchema::Scope)
                ?.default?.toFloatOrNull()
        }

    @Composable
    override fun show(scope: Any?) {

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