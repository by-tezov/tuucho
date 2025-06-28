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
import com.tezov.tuucho.core.domain._system.stringOrNull
import com.tezov.tuucho.core.domain.schema.ComponentSchema
import com.tezov.tuucho.core.domain.schema.StyleSchema
import com.tezov.tuucho.core.domain.schema._element.spacer.SpacerSchema
import com.tezov.tuucho.core.domain.schema.common.SubsetSchema.Companion.subsetOrNull
import com.tezov.tuucho.core.domain.schema.common.TypeSchema
import com.tezov.tuucho.core.domain.schema.common.TypeSchema.Companion.typeOrNull
import com.tezov.tuucho.core.ui.renderer._system.ComposableScreenProtocol
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject

class SpacerRendered : Renderer() {

    override fun accept(jsonObject: JsonObject): Boolean {
        return jsonObject.typeOrNull == TypeSchema.Value.Type.component &&
                jsonObject.subsetOrNull == SpacerSchema.Default.subset
    }

    override fun process(jsonObject: JsonObject): ComposableScreenProtocol {
        val style = jsonObject[ComponentSchema.Key.style]!!.jsonObject
        val width = style[StyleSchema.Key.width].stringOrNull?.toIntOrNull()?.dp
        val height = style[StyleSchema.Key.height].stringOrNull?.toIntOrNull()?.dp
        val weight = style[StyleSchema.Key.weight].stringOrNull?.toFloatOrNull()
        return SpacerScreen(
            width = width,
            height = height,
            weight = weight
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