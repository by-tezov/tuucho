package com.tezov.tuucho.uiComponent.stable.presentation.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.Image
import coil3.compose.asPainter
import com.tezov.tuucho.core.domain.business._system.koin.KoinIsolatedContext
import com.tezov.tuucho.core.domain.business.protocol.repository.ImageRepositoryProtocol
import com.tezov.tuucho.core.presentation.tool.modifier.thenIfNotNull
import com.tezov.tuucho.core.presentation.ui._system.subset
import com.tezov.tuucho.core.presentation.ui.render.projection.ColorProjectionProtocol
import com.tezov.tuucho.core.presentation.ui.render.projection.ImageProjectionProtocol
import com.tezov.tuucho.core.presentation.ui.render.projection.TextProjectionProtocol
import com.tezov.tuucho.core.presentation.ui.render.projection.color
import com.tezov.tuucho.core.presentation.ui.render.projection.dimension.DpProjectionProtocol
import com.tezov.tuucho.core.presentation.ui.render.projection.dimension.FloatProjectionProtocol
import com.tezov.tuucho.core.presentation.ui.render.projection.dimension.StringProjectionProtocol
import com.tezov.tuucho.core.presentation.ui.render.projection.dimension.dp
import com.tezov.tuucho.core.presentation.ui.render.projection.dimension.float
import com.tezov.tuucho.core.presentation.ui.render.projection.dimension.mutable
import com.tezov.tuucho.core.presentation.ui.render.projection.dimension.string
import com.tezov.tuucho.core.presentation.ui.render.projection.image
import com.tezov.tuucho.core.presentation.ui.render.projection.mutable
import com.tezov.tuucho.core.presentation.ui.render.projection.text
import com.tezov.tuucho.core.presentation.ui.render.projector.componentProjector
import com.tezov.tuucho.core.presentation.ui.render.projector.content
import com.tezov.tuucho.core.presentation.ui.render.projector.contextual
import com.tezov.tuucho.core.presentation.ui.render.projector.style
import com.tezov.tuucho.core.presentation.ui.screen.protocol.ScreenContextProtocol
import com.tezov.tuucho.core.presentation.ui.view.AbstractView
import com.tezov.tuucho.core.presentation.ui.view.protocol.ViewFactoryProtocol
import com.tezov.tuucho.core.presentation.ui.view.protocol.ViewProtocol
import com.tezov.tuucho.uiComponent.stable.domain.jsonSchema.material.ImageSchema.Component
import com.tezov.tuucho.uiComponent.stable.domain.jsonSchema.material.ImageSchema.Content
import com.tezov.tuucho.uiComponent.stable.domain.jsonSchema.material.ImageSchema.Style
import kotlinx.serialization.json.JsonObject

interface ImageViewProtocol : ViewProtocol {
    @Composable
    fun ComposeComponent(
        width: Dp?,
        height: Dp?,
        shape: String?,
        padding: Dp?,
        alpha: Float?,
        backgroundColor: Color?,
        tintColor: Color?,
        image: ImageRepositoryProtocol.Image<Image>?,
        description: String?
    )

    @Composable
    fun ComposePlaceHolder()
}

class ImageViewFactory : ViewFactoryProtocol {
    companion object {
        fun createImageView(
            screenContext: ScreenContextProtocol,
        ): ImageViewProtocol = ImageView(
            screenContext = screenContext
        )
    }

    override fun accept(
        componentObject: JsonObject,
    ) = componentObject.subset == Component.Value.subset

    override suspend fun process(
        screenContext: ScreenContextProtocol,
    ): ImageViewProtocol = createImageView(
        screenContext = screenContext,
    )
}

private class ImageView(
    screenContext: ScreenContextProtocol,
) : AbstractView(screenContext),
    ImageViewProtocol {
    private lateinit var width: DpProjectionProtocol
    private lateinit var height: DpProjectionProtocol
    private lateinit var shape: StringProjectionProtocol
    private lateinit var padding: DpProjectionProtocol
    private lateinit var backgroundColor: ColorProjectionProtocol
    private lateinit var alpha: FloatProjectionProtocol
    private lateinit var tintColor: ColorProjectionProtocol
    private lateinit var image: ImageProjectionProtocol
    private lateinit var description: TextProjectionProtocol

    override suspend fun createComponentProjector() = componentProjector {
        +style {
            width = +dp(Style.Key.width).mutable
            height = +dp(Style.Key.height).mutable
            shape = +string(Style.Key.shape).mutable
            padding = +dp(Style.Key.padding).mutable
            alpha = +float(Style.Key.alpha).mutable
            backgroundColor = +color(Style.Key.backgroundColor).mutable
            tintColor = +color(Style.Key.tintColor).mutable
        }
        +content {
            image = +image(Content.Key.value)
            description = +text(Content.Key.description)
        }.contextual
    }.contextual

    override fun getResolvedStatus() = true

    @Composable
    override fun displayComponent(
        scope: Any?
    ) {
        ComposeComponent(
            width = width.value,
            height = height.value,
            shape = shape.value,
            padding = padding.value,
            alpha = alpha.value,
            backgroundColor = backgroundColor.value,
            tintColor = tintColor.value,
            image = image.value,
            description = description.value,
        )
    }

    @Composable
    override fun ComposeComponent(
        width: Dp?,
        height: Dp?,
        shape: String?,
        padding: Dp?,
        alpha: Float?,
        backgroundColor: Color?,
        tintColor: Color?,
        image: ImageRepositoryProtocol.Image<Image>?,
        description: String?
    ) {
        val density = LocalDensity.current
        val _width = remember(image?.width) {
            width ?: with(density) { image?.width?.toDp() }
        }
        val _height = remember(image?.height) {
            height ?: with(density) { image?.height?.toDp() }
        }
        val resolvedShape: Modifier.(shape: String?) -> Modifier = remember(shape) {
            {
                when (shape) {
                    Style.Value.Shape.rounded -> {
                        clip(CircleShape)
                    }

                    Style.Value.Shape.roundedSquare -> {
                        clip(RoundedCornerShape(size = 12.dp))
                    }

                    else -> {
                        this
                    }
                }
            }
        }
        val _tintColor = tintColor?.let {
            ColorFilter.colorMatrix(
                ColorMatrix().apply {
                    setToScale(tintColor.red, tintColor.green, tintColor.blue, 1f)
                }
            )
        }
        Box(
            modifier = Modifier
                .thenIfNotNull(_width) { width(it) }
                .thenIfNotNull(_height) { height(it) }
                .thenIfNotNull(shape) { resolvedShape(it) }
                .thenIfNotNull(backgroundColor) { background(it) }
                .thenIfNotNull(padding) { padding(it) }
        ) {
            image?.let {
                Image(
                    painter = image.source.asPainter(
                        context = KoinIsolatedContext.koin.get() // TODO
                    ),
                    contentScale = ContentScale.FillBounds,
                    contentDescription = description,
                    modifier = Modifier
                        .fillMaxSize()
                        .thenIfNotNull(shape) { resolvedShape(it) },
                    colorFilter = tintColor?.let { _tintColor },
                    alpha = alpha ?: DefaultAlpha
                )
            }
        }
    }

    @Composable
    override fun ComposePlaceHolder() {
        displayPlaceholder(null)
    }
}
