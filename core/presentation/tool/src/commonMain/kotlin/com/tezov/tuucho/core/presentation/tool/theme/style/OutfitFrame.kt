package com.tezov.tuucho.core.presentation.tool.theme.style

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.tezov.tuucho.core.presentation.tool.delegate.DelegateNullFallBack

fun Modifier.background(
    style: OutfitFrame.StateColor.Style,
    selector: Any? = null,
): Modifier {
    val sketch = style.outfitShape.resolve(selector)
    val border = style.outfitBorder.resolve(selector)
    return if (border != null) {
        border(border, sketch).background(sketch, false)
    } else if (sketch != null) {
        background(sketch)
    } else {
        this
    }
}

typealias OutfitFrameStateColor = OutfitFrame.StateColor.Style

object OutfitFrame {

    object StateColor {

        class StyleBuilder internal constructor(style: Style) {
            var outfitShape = style.outfitShape
            var outfitBorder = style.outfitBorder

            internal fun get() = Style(
                outfitShape = outfitShape,
                outfitBorder = outfitBorder,
            )
        }

        class Style(
            outfitShape: OutfitShapeStateColor? = null,
            outfitBorder: OutfitBorderStateColor? = null,
        ) {
            val outfitShape: OutfitShapeStateColor by DelegateNullFallBack.Ref(
                outfitShape,
                fallBackValue = { com.tezov.tuucho.core.presentation.tool.theme.style.OutfitShapeStateColor() }
            )
            val outfitBorder: OutfitBorderStateColor by DelegateNullFallBack.Ref(
                outfitBorder,
                fallBackValue = { com.tezov.tuucho.core.presentation.tool.theme.style.OutfitBorderStateColor() }
            )

            companion object {

                @Composable
                fun Style.copy(builder: @Composable StyleBuilder.() -> Unit = {}) =
                    StyleBuilder(this).also {
                        it.builder()
                    }.get()

            }

            constructor(style: Style) : this(
                outfitShape = style.outfitShape,
                outfitBorder = style.outfitBorder,
            )

            fun getShape() = outfitShape.getShape()

            fun resolveColorShape(selector: Any? = null) = outfitShape.resolveColor(selector)

            fun resolveShape(selector: Any? = null) = outfitShape.resolve(selector)

            fun resolveColorBorder(selector: Any? = null) = outfitBorder.resolveColor(selector)

            fun resolveBorder(selector: Any? = null) = outfitBorder.resolve(selector)

        }

    }


}

