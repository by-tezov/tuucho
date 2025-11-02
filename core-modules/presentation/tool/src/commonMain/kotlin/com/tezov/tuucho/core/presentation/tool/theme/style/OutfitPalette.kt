package com.tezov.tuucho.core.presentation.tool.theme.style

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.tezov.tuucho.core.presentation.tool.delegate.DelegateNullFallBack
import com.tezov.tuucho.core.presentation.tool.type.primaire.DpSize
import androidx.compose.ui.graphics.Color as ColorImport

typealias OutfitPaletteColor = OutfitPalette.Color.Style
typealias OutfitPaletteColorBiStable = OutfitStateBiStable<OutfitPaletteColor>
typealias OutfitPaletteColorSemantic = OutfitStateSemantic<OutfitPaletteColor>

typealias OutfitPaletteSize<T> = OutfitPalette.Size.Style<T>
typealias OutfitPaletteDirection<T> = OutfitPalette.Direction.Style<T>

fun Modifier.padding(
    padding: OutfitPalette.Direction.Style<Dp>,
) = this.padding(vertical = padding.vertical, horizontal = padding.horizontal)

object OutfitPalette {
    object Color {
        class StyleBuilder internal constructor(
            val style: Style
        ) {
            var default = style.default
            var dark = style.dark
            var fade = style.fade
            var shady = style.shady
            var light = style.light
            var shiny = style.shiny
            var accent = style.accent
            var decor = style.decor
            var overlay = style.overlay

            internal fun get() = Style(
                default = default,
                dark = dark,
                fade = fade,
                shady = shady,
                light = light,
                shiny = shiny,
                accent = accent,
                decor = decor,
                overlay = overlay,
            )
        }

        class Style(
            val default: ColorImport,
            dark: ColorImport? = null,
            fade: ColorImport? = null,
            shady: ColorImport? = null,
            light: ColorImport? = null,
            shiny: ColorImport? = null,
            accent: ColorImport? = null,
            decor: ColorImport? = null,
            overlay: ColorImport? = null,
        ) {
            private val delegates = DelegateNullFallBack.Group<ColorImport>()
            val dark: ColorImport by delegates.ref(dark)
            val fade: ColorImport by delegates.ref(fade)
            val shady: ColorImport by delegates.ref(shady)
            val shiny: ColorImport by delegates.ref(shiny)
            val light: ColorImport by delegates.ref(light)
            val accent: ColorImport by delegates.ref(accent)
            val decor: ColorImport by delegates.ref(decor)
            val overlay: ColorImport by delegates.ref(overlay)

            init {
                delegates.fallBackValue = { default }
            }

            companion object {
                @Composable
                fun Style.copy(
                    builder: @Composable StyleBuilder.() -> Unit = {}
                ) = StyleBuilder(this)
                    .also {
                        it.builder()
                    }.get()

                inline val ColorImport.asPaletteColor: Style
                    get() = Style(default = this)
            }

            constructor(style: Style) : this(
                default = style.default,
                dark = style.dark,
                shady = style.shady,
                fade = style.fade,
                light = style.light,
                shiny = style.shiny,
                accent = style.accent,
                decor = style.decor,
                overlay = style.overlay,
            )
        }
    }

    object Size {
        class StyleBuilder<T : Any> internal constructor(
            val style: Style<T>
        ) {
            var micro = style.micro
            var small = style.small
            var normal = style.normal
            var big = style.big
            var huge = style.huge
            var supra = style.supra

            internal fun get() = Style(
                micro = micro,
                small = small,
                normal = normal,
                big = big,
                huge = huge,
                supra = supra,
            )
        }

        class Style<T : Any>(
            val normal: T,
            micro: T? = null,
            small: T? = null,
            big: T? = null,
            huge: T? = null,
            supra: T? = null,
        ) {
            private val delegates = DelegateNullFallBack.Group<T>()
            val micro: T by delegates.ref(micro)
            val small: T by delegates.ref(small)
            val big: T by delegates.ref(big)
            val huge: T by delegates.ref(huge)
            val supra: T by delegates.ref(supra)

            init {
                delegates.fallBackValue = { normal }
            }

            companion object {
                @Composable
                fun <T : Any> Style<T>.copy(
                    builder: @Composable StyleBuilder<T>.() -> Unit = {}
                ) = StyleBuilder(this)
                    .also {
                        it.builder()
                    }.get()

                inline val Dp.asPaletteSize: Style<Dp>
                    get() = Style(normal = this)

                inline val DpSize.asPaletteSize: Style<DpSize>
                    get() = Style(normal = this)

                inline val <T : Any> OutfitPaletteDirection<T>.asPaletteSize: Style<OutfitPaletteDirection<T>>
                    get() = Style(normal = this)
            }

            constructor(style: Style<T>) : this(
                micro = style.micro,
                small = style.small,
                normal = style.normal,
                big = style.big,
                huge = style.huge,
                supra = style.supra,
            )
        }
    }

    object Direction {
        class StyleBuilder<T : Any> internal constructor(
            val style: Style<T>
        ) {
            var vertical = style.vertical
            var horizontal = style.horizontal

            internal fun get() = Style(
                vertical = vertical,
                horizontal = horizontal,
            )
        }

        class Style<T : Any>(
            vertical: T? = null,
            horizontal: T? = null,
        ) {
            private val delegates = DelegateNullFallBack.Group<T>()
            val vertical: T by delegates.ref(vertical)
            val horizontal: T by delegates.ref(horizontal)

            init {
                delegates.fallBackValue = delegates.firstNotNull()?.fallBackValue
            }

            companion object {
                @Composable
                fun <T : Any> Style<T>.copy(
                    builder: @Composable StyleBuilder<T>.() -> Unit = {}
                ) = StyleBuilder(this).also {
                    it.builder()
                }

                inline val <T : Any> Size.Style<T>.asPaletteDirection: Style<Size.Style<T>>
                    get() = Style(all = this)

                inline val Dp.asPaletteDirection: Style<Dp>
                    get() = Style(all = this)

                inline val DpSize.asPaletteDirection: Style<DpSize>
                    get() = Style(all = this)
            }

            constructor(all: T? = null) : this(
                vertical = all,
                horizontal = all,
            )

            constructor(style: Style<T>) : this(
                vertical = style.vertical,
                horizontal = style.horizontal,
            )
        }
    }
}
