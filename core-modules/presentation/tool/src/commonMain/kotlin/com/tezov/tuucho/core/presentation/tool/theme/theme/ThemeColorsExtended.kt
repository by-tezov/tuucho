package com.tezov.tuucho.core.presentation.tool.theme.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tezov.tuucho.core.presentation.tool.delegate.DelegateNullFallBack
import com.tezov.tuucho.core.presentation.tool.theme.style.OutfitBorderStateColor
import com.tezov.tuucho.core.presentation.tool.theme.style.OutfitFrameStateColor
import com.tezov.tuucho.core.presentation.tool.theme.style.OutfitPaletteColor
import com.tezov.tuucho.core.presentation.tool.theme.style.OutfitPaletteColorSemantic
import com.tezov.tuucho.core.presentation.tool.theme.style.OutfitShape.Size.Companion.asShapeSize
import com.tezov.tuucho.core.presentation.tool.theme.style.OutfitShapeStateColor
import com.tezov.tuucho.core.presentation.tool.theme.style.OutfitState.Simple.Style.Companion.asStateSimple
import com.tezov.tuucho.core.presentation.tool.theme.style.OutfitText.StateColor.Style.Companion.asTextStateColor

val MaterialTheme.colorsResource
    @Composable
    @ReadOnlyComposable
    get() = ThemeColorsExtended.localResources.current

val MaterialTheme.colorsExtended
    @Composable
    @ReadOnlyComposable
    get() = ThemeColorsExtended.localCommon.current

infix fun MaterialTheme.provides(
    value: ThemeColorsExtended.Common
) = ThemeColorsExtended.localCommon provides value

object ThemeColorsExtended {
    object Dummy {
        val pink: Color = Color(0xFFFF00E5)
        val green: Color = Color(0xFF39EC07)
        val blue: Color = Color(0xFFA05400)

        val outfitShapeState
            get() = OutfitShapeStateColor(
                outfitState = pink.asStateSimple,
                size = 12.dp.asShapeSize
            )
        val outfitBorderState
            get() = OutfitBorderStateColor(
                size = 2.dp,
                outfitState = blue.asStateSimple,
            )
        val outfitFrameState
            get() = OutfitFrameStateColor(
                outfitShape = outfitShapeState,
                outfitBorder = outfitBorderState,
            )
        val textStyle
            get() = TextStyle(
                color = green,
                fontSize = 14.sp
            )
        val outfitTextState get() = textStyle.asTextStateColor
    }

    object Resources {
        val transparent: Color = Color.Transparent
        val red: Color = Color(0xFFFF0000)
        val green: Color = Color(0xFF00FF00)
        val blue: Color = Color(0xFF0000FF)
        val yellow: Color = Color(0xFFFFFF00)
        val black: Color = Color(0xFF000000)
        val white: Color = Color(0xFFFFFFFF)
        val cian: Color = Color(0xFF00FFFF)
        val magenta: Color = Color(0xFFFF00FF)
        val gray: Color = Color(0xFF888888)
        val orange: Color = Color(0xFFD37A73)
        val blueLight: Color = Color(0xFF6741BB)
        val greenMelon: Color = Color(0xFF52E057)
    }

    internal val localResources = staticCompositionLocalOf { Resources }

    class Common(
        val background: OutfitPaletteColor,
        val onBackground: OutfitPaletteColor,
        val primary: OutfitPaletteColor,
        val onPrimary: OutfitPaletteColor,
        ribbon: OutfitPaletteColor? = null,
        backgroundElevated: OutfitPaletteColor? = null,
        onBackgroundElevated: OutfitPaletteColor? = null,
        backgroundModal: OutfitPaletteColor? = null,
        onBackgroundModal: OutfitPaletteColor? = null,
        secondary: OutfitPaletteColor? = null,
        onSecondary: OutfitPaletteColor? = null,
        tertiary: OutfitPaletteColor? = null,
        onTertiary: OutfitPaletteColor? = null,
        semantic: OutfitPaletteColorSemantic? = null,
        onSemantic: OutfitPaletteColorSemantic? = null,
    ) {
        val ribbon: OutfitPaletteColor by DelegateNullFallBack.Ref(
            ribbon,
            fallBackValue = { primary }
        )

        val backgroundElevated: OutfitPaletteColor by DelegateNullFallBack.Ref(
            backgroundElevated,
            fallBackValue = { background }
        )
        val onBackgroundElevated: OutfitPaletteColor by DelegateNullFallBack.Ref(
            onBackgroundElevated,
            fallBackValue = { onBackground }
        )

        val backgroundModal: OutfitPaletteColor by DelegateNullFallBack.Ref(
            backgroundModal,
            fallBackValue = { background }
        )
        val onBackgroundModal: OutfitPaletteColor by DelegateNullFallBack.Ref(
            onBackgroundModal,
            fallBackValue = { onBackground }
        )

        val secondary: OutfitPaletteColor by DelegateNullFallBack.Ref(
            secondary,
            fallBackValue = { primary }
        )
        val onSecondary: OutfitPaletteColor by DelegateNullFallBack.Ref(
            onSecondary,
            fallBackValue = { onPrimary }
        )
        val tertiary: OutfitPaletteColor by DelegateNullFallBack.Ref(
            tertiary,
            fallBackValue = { primary }
        )
        val onTertiary: OutfitPaletteColor by DelegateNullFallBack.Ref(
            onTertiary,
            fallBackValue = { onPrimary }
        )

        val semantic: OutfitPaletteColorSemantic by DelegateNullFallBack.Ref(
            semantic,
            fallBackValue = {
                OutfitPaletteColorSemantic(
                    primary
                )
            }
        )
        val onSemantic: OutfitPaletteColorSemantic by DelegateNullFallBack.Ref(
            onSemantic,
            fallBackValue = {
                OutfitPaletteColorSemantic(
                    onPrimary
                )
            }
        )
    }

    internal val localCommon: ProvidableCompositionLocal<Common> = staticCompositionLocalOf {
        error("not provided")
    }

    @Composable
    fun materialLight() = ColorScheme(
        primary = MaterialTheme.colorsExtended.primary.default,
//        primaryVariant = MaterialTheme.colorsExtended.primary.accent,
        onPrimary = MaterialTheme.colorsExtended.onPrimary.default,
        secondary = MaterialTheme.colorsExtended.secondary.default,
//        secondaryVariant = MaterialTheme.colorsExtended.secondary.accent,
        onSecondary = MaterialTheme.colorsExtended.onSecondary.default,
        surface = Color.Transparent,
        onSurface = MaterialTheme.colorsExtended.onBackground.default,
        background = MaterialTheme.colorsExtended.background.default,
        onBackground = MaterialTheme.colorsExtended.onBackground.default,
        error = MaterialTheme.colorsExtended.semantic.error.default,
        onError = MaterialTheme.colorsExtended.onSemantic.error.default,
//        isLight = true,
        // MISSING FIELDS
        inversePrimary = Color.Red,
        primaryContainer = Color.Red,
        onPrimaryContainer = Color.Red,
        secondaryContainer = Color.Red,
        onSecondaryContainer = Color.Red,
        errorContainer = Color.Red,
        onErrorContainer = Color.Red,
        tertiary = Color.Red,
        onTertiary = Color.Red,
        tertiaryContainer = Color.Red,
        onTertiaryContainer = Color.Red,
        surfaceVariant = Color.Red,
        onSurfaceVariant = Color.Red,
        surfaceTint = Color.Red,
        inverseSurface = Color.Red,
        inverseOnSurface = Color.Red,
        outline = Color.Red,
        outlineVariant = Color.Red,
        scrim = Color.Red,
        surfaceBright = Color.Red,
        surfaceDim = Color.Red,
        surfaceContainer = Color.Red,
        surfaceContainerHigh = Color.Red,
        surfaceContainerHighest = Color.Red,
        surfaceContainerLow = Color.Red,
        surfaceContainerLowest = Color.Red,
    )
}
