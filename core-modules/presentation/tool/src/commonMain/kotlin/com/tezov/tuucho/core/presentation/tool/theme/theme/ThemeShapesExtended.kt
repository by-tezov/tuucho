package com.tezov.tuucho.core.presentation.tool.theme.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.tezov.tuucho.core.presentation.tool.delegate.DelegateNullFallBack
import com.tezov.tuucho.core.presentation.tool.theme.style.OutfitPaletteSize
import com.tezov.tuucho.core.presentation.tool.theme.style.OutfitShape.StateColor.Style.Companion.asPaletteSize
import com.tezov.tuucho.core.presentation.tool.theme.style.OutfitShapeStateColor

val MaterialTheme.shapesExtended
    @Composable
    @ReadOnlyComposable
    get() = ThemeShapesExtended.localCommon.current

infix fun MaterialTheme.provides(value: ThemeShapesExtended.Common) =
    ThemeShapesExtended.localCommon provides value

object ThemeShapesExtended {

    class Common(
        cluster: OutfitPaletteSize<OutfitShapeStateColor>? = null,
        block: OutfitPaletteSize<OutfitShapeStateColor>? = null,
        element: OutfitPaletteSize<OutfitShapeStateColor>? = null,
        chunk: OutfitPaletteSize<OutfitShapeStateColor>? = null,
        button: OutfitPaletteSize<OutfitShapeStateColor>? = null,
        icon: OutfitPaletteSize<OutfitShapeStateColor>? = null,
        clip: OutfitPaletteSize<Shape>? = null,
    ) {

        private val delegates =
            DelegateNullFallBack.Group<OutfitPaletteSize<OutfitShapeStateColor>>()
        val cluster: OutfitPaletteSize<OutfitShapeStateColor> by delegates.ref(cluster)
        val block: OutfitPaletteSize<OutfitShapeStateColor> by delegates.ref(block)
        val element: OutfitPaletteSize<OutfitShapeStateColor> by delegates.ref(element)
        val chunk: OutfitPaletteSize<OutfitShapeStateColor> by delegates.ref(chunk)
        val button: OutfitPaletteSize<OutfitShapeStateColor> by delegates.ref(button)
        val icon: OutfitPaletteSize<OutfitShapeStateColor> by delegates.ref(icon)
        val clip: OutfitPaletteSize<Shape> by DelegateNullFallBack.Ref(clip, fallBackValue = {
            OutfitPaletteSize(normal = RoundedCornerShape(4.dp))
        })

        init {
            delegates.fallBackValue = {
                ThemeColorsExtended.Dummy.outfitShapeState.asPaletteSize
            }
        }

    }

    internal val localCommon: ProvidableCompositionLocal<Common> = staticCompositionLocalOf {
        error("not provided")
    }

}