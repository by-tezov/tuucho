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
import com.tezov.tuucho.core.domain.schema.ComponentSchema.Companion.styleObjectOrNull
import com.tezov.tuucho.core.domain.schema.StyleSchema.Companion.heightOrNull
import com.tezov.tuucho.core.domain.schema.StyleSchema.Companion.widthOrNull
import com.tezov.tuucho.core.domain.schema.SubsetSchema.Companion.subsetOrNull
import com.tezov.tuucho.core.domain.schema.TypeSchema
import com.tezov.tuucho.core.domain.schema.TypeSchema.Companion.typeOrNull
import com.tezov.tuucho.core.domain.schema._element.SpacerSchema
import com.tezov.tuucho.core.domain.schema._element.SpacerSchema.Style.weightOrNull
import com.tezov.tuucho.core.ui.renderer._system.ComposableScreenProtocol
import kotlinx.serialization.json.JsonElement

class SpacerRendered : Renderer() {

    override fun accept(materialElement: JsonElement): Boolean {
        return materialElement.typeOrNull == TypeSchema.Value.Type.component &&
                materialElement.subsetOrNull == SpacerSchema.Component.Value.subset
    }

    override fun process(materialElement: JsonElement): ComposableScreenProtocol {
        val style = materialElement.styleObjectOrNull

        return SpacerScreen(
            width = style?.widthOrNull?.toIntOrNull()?.dp,
            height = style?.heightOrNull?.toIntOrNull()?.dp,
            weight = style?.weightOrNull?.toFloatOrNull()
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