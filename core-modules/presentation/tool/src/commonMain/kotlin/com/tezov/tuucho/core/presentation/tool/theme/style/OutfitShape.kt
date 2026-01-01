package com.tezov.tuucho.core.presentation.tool.theme.style

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tezov.tuucho.core.presentation.tool.delegate.DelegateNullFallBack
import com.tezov.tuucho.core.presentation.tool.modifier.thenIfTrue
import com.tezov.tuucho.core.presentation.tool.theme.style.OutfitShape.Size.Companion.asShapeSize
import androidx.compose.ui.graphics.Color as ColorImport

fun Modifier.background(
    style: OutfitShape.StateColor.Style?,
    selector: Any? = null,
) = background(style?.resolve(selector))

fun Modifier.background(
    sketch: OutfitShape.Sketch?,
    clip: Boolean = true,
) = sketch?.takeIf { it.color != null }?.let {
    thenIfTrue(clip) {
        clip(it.shape)
    }.background(it.color!!, it.shape)
} ?: this

typealias OutfitShapeStateColor = OutfitShape.StateColor.Style

object OutfitShape {
    enum class Template {
        Rounded;

        fun get(
            size: Size? = null
        ) = size?.let {
            when (this) {
                Rounded -> RoundedCornerShape(
                    topStart = it.topStart ?: CornerSize(0),
                    topEnd = it.topEnd ?: CornerSize(0),
                    bottomStart = it.bottomStart ?: CornerSize(0),
                    bottomEnd = it.bottomEnd ?: CornerSize(0)
                )
            }
        }

        fun get(
            size: Size? = null,
            color: ColorImport? = null
        ) = get(size)?.let {
            Sketch(it, color)
        }
    }

    class Size(
        val topStart: CornerSize? = null,
        val topEnd: CornerSize? = null,
        val bottomStart: CornerSize? = null,
        val bottomEnd: CornerSize? = null,
    ) {
        companion object {
            inline val Int.asShapeSize: Size get() = Size(this)

            inline val Dp.asShapeSize: Size get() = Size(this)
        }

        val firstNotNull get() = topStart ?: topEnd ?: bottomStart ?: bottomEnd

        constructor(
            topStart: Int? = null,
            topEnd: Int? = null,
            bottomStart: Int? = null,
            bottomEnd: Int? = null,
        ) : this(
            topStart?.let { CornerSize(it) },
            topEnd?.let { CornerSize(it) },
            bottomStart?.let { CornerSize(it) },
            bottomEnd?.let { CornerSize(it) },
        )

        constructor(
            topStart: Dp? = null,
            topEnd: Dp? = null,
            bottomStart: Dp? = null,
            bottomEnd: Dp? = null,
        ) : this(
            topStart?.let { CornerSize(it) },
            topEnd?.let { CornerSize(it) },
            bottomStart?.let { CornerSize(it) },
            bottomEnd?.let { CornerSize(it) },
        )

        constructor(size: CornerSize) : this(size, size, size, size)
        constructor(percent: Int) : this(CornerSize(percent))
        constructor(size: Dp) : this(CornerSize(size))
    }

    data class Sketch(
        val shape: Shape,
        val color: ColorImport? = null
    )

    object StateColor {
        class StyleBuilder internal constructor(
            style: Style
        ) {
            var template = style.template
            var size = style.size
            var outfitState = style.outfitState

            fun get() = Style(
                template = template,
                size = size,
                outfitState = outfitState,
            )
        }

        class Style(
            val template: Template = Template.Rounded,
            val size: Size? = null,
            outfitState: OutfitState.Style<ColorImport>? = null,
        ) {
            val outfitState: OutfitState.Style<ColorImport> by DelegateNullFallBack.Ref(
                outfitState,
                fallBackValue = {
                    com.tezov.tuucho.core.presentation.tool.theme.style
                        .OutfitStateNull()
                }
            )

            companion object {
                @Composable
                fun Style.copy(
                    scope: @Composable StyleBuilder.() -> Unit = {}
                ) = StyleBuilder(this)
                    .also {
                        it.scope()
                    }.get()

                inline val OutfitShapeStateColor.asPaletteSize: OutfitPaletteSize<OutfitShapeStateColor>
                    get() = OutfitPaletteSize(
                        normal = this
                    )

                inline val Dp.asStateColor: OutfitShapeStateColor
                    get() = OutfitShapeStateColor(size = this.asShapeSize)

                inline val Int.asStateColor: OutfitShapeStateColor
                    get() = OutfitShapeStateColor(size = this.asShapeSize)

                inline val OutfitStateSimple<ColorImport>.asStateColor: OutfitShapeStateColor
                    get() = OutfitShapeStateColor(size = 0.dp.asShapeSize, outfitState = this)

                inline val OutfitStateBiStable<ColorImport>.asStateColor: OutfitShapeStateColor
                    get() = OutfitShapeStateColor(size = 0.dp.asShapeSize, outfitState = this)

                inline val OutfitStateSemantic<ColorImport>.asStateColor: OutfitShapeStateColor
                    get() = OutfitShapeStateColor(size = 0.dp.asShapeSize, outfitState = this)

                inline val OutfitShapeStateColor.asFrameStateColor: OutfitFrameStateColor
                    get() = com.tezov.tuucho.core.presentation.tool.theme.style.OutfitFrameStateColor(
                        outfitShape = this
                    )
            }

            constructor(style: Style) : this(
                template = style.template,
                size = style.size,
                outfitState = style.outfitState,
            )

            fun getShape() = template.get(size)

            fun resolveColor(
                selector: Any? = null
            ) = outfitState.resolve(selector, ColorImport::class)

            fun resolve(
                selector: Any? = null
            ) = template.get(size, resolveColor(selector))
        }
    }
}
