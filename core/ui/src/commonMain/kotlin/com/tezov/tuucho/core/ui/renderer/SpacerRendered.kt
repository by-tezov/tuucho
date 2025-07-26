package com.tezov.tuucho.core.ui.renderer

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

import com.tezov.tuucho.core.domain.model.schema.material.DimensionSchema
import com.tezov.tuucho.core.domain.model.schema.material.SubsetSchema
import com.tezov.tuucho.core.domain.model.schema.material.TypeSchema
import com.tezov.tuucho.core.domain.model.schema.material._element.SpacerSchema
import com.tezov.tuucho.core.ui.renderer._system.ComposableScreenProtocol
import kotlinx.serialization.json.JsonElement

class SpacerRendered : Renderer() {

    override fun accept(element: JsonElement) = element
        .let {
            it.withScope(TypeSchema::Scope).self == TypeSchema.Value.component &&
            it.withScope(SubsetSchema::Scope).self == SpacerSchema.Component.Value.subset
        }

    override fun process(element: JsonElement): ComposableScreenProtocol {
        val style = element.onScope(SpacerSchema.Style::Scope)

        val width = style.width
            ?.withScope(DimensionSchema::Scope)?.default
        val height = style.height
            ?.withScope(DimensionSchema::Scope)?.default
        val weight = style.weight
            ?.withScope(DimensionSchema::Scope)?.default

        return SpacerScreen(
            width = width?.toIntOrNull()?.dp,
            height = height?.toIntOrNull()?.dp,
            weight = weight?.toFloatOrNull()
        )
    }
}

class SpacerScreen(
    val width: Dp?,
    val height: Dp?,
    val weight: Float?
) : ComposableScreenProtocol() {

    @Composable
    override fun show(scope: Any?) {

        //TODO do much better than that

        var modifier: Modifier = Modifier
        if (weight != null) {
            scope.apply {
                when (this) {
                    is ColumnScope -> modifier = modifier.weight(weight)
                    is RowScope -> modifier = modifier.weight(weight)
                }
            }
        } else {
            if (width != null) modifier = modifier.width(width)
            if (height != null) modifier = modifier.height(height)
        }
        Spacer(
            modifier = modifier
                .background(color = Color.Gray)
        )
    }
}