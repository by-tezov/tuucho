package com.tezov.tuucho.core.presentation.ui.preview

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import coil3.ColorImage
import com.tezov.tuucho.core.domain.business.protocol.repository.ImageRepositoryProtocol
import com.tezov.tuucho.core.presentation.ui.render.projection.Image

object DummyImage {
    operator fun invoke(
        color: Color,
        width: Int = 128,
        height: Int = 128,
        size: Long = 128 * 128,
    ): Image = Image(
        coilImage = object : ImageRepositoryProtocol.Image<coil3.Image> {
            override val source: coil3.Image = ColorImage(
                color = color.toArgb(),
                width = width,
                height = height,
                size = size,
            )
            override val tags = null
            override val tagsExcluder = null
            override val size: Long
                get() = source.size
            override val width: Int
                get() = source.width
            override val height: Int
                get() = source.width
        }
    )
}
