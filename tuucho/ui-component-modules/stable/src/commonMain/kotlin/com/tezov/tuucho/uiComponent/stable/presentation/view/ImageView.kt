package com.tezov.tuucho.uiComponent.stable.presentation.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import coil3.Canvas
import com.tezov.tuucho.core.domain.business.protocol.repository.ImageRepositoryProtocol
import com.tezov.tuucho.core.presentation.ui._system.subset
import com.tezov.tuucho.core.presentation.ui.render.projection.ImageProjectionProtocol
import com.tezov.tuucho.core.presentation.ui.render.projection.image
import com.tezov.tuucho.core.presentation.ui.render.projector.componentProjector
import com.tezov.tuucho.core.presentation.ui.render.projector.content
import com.tezov.tuucho.core.presentation.ui.render.projector.contextual
import com.tezov.tuucho.core.presentation.ui.screen.protocol.ScreenContextProtocol
import com.tezov.tuucho.core.presentation.ui.view.AbstractView
import com.tezov.tuucho.core.presentation.ui.view.protocol.ViewFactoryProtocol
import com.tezov.tuucho.core.presentation.ui.view.protocol.ViewProtocol
import com.tezov.tuucho.uiComponent.stable.domain.jsonSchema.material.ImageSchema.Component
import com.tezov.tuucho.uiComponent.stable.domain.jsonSchema.material.ImageSchema.Content
import kotlinx.serialization.json.JsonObject

interface ImageViewProtocol : ViewProtocol {
    @Composable
    fun ComposeComponent(
        image: ImageRepositoryProtocol.Image<coil3.Image, Canvas>?
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
    private lateinit var image: ImageProjectionProtocol

    override suspend fun createComponentProjector() = componentProjector {
        +content {
            image = +image(
                key = Content.Key.value,
            )
        }.contextual
    }.contextual

    override fun getResolvedStatus() = true

    @Composable
    override fun displayComponent(
        scope: Any?
    ) {
        ComposeComponent(
            image = image.value
        )
    }

    @Composable
    override fun ComposeComponent(
        image: ImageRepositoryProtocol.Image<coil3.Image, Canvas>?
    ) {
        image?.let {
            Image(
                painter = CoilImagePainter(image),
                contentDescription = "Image",
                modifier = Modifier
                    .size(72.dp)
                    .background(Color.Red)
                    .padding(6.dp),
//            colorFilter = ColorFilter.tint(Color.Blue)
            )
        } ?: run {
            Text("Image coming soon")
        }
    }

    @Composable
    override fun ComposePlaceHolder() {
        displayPlaceholder(null)
    }
}

class CoilImagePainter(
    private val image: ImageRepositoryProtocol.Image<coil3.Image, Canvas>
) : Painter() {
    override val intrinsicSize = androidx.compose.ui.geometry.Size(
        image.width.toFloat(),
        image.height.toFloat()
    )

    override fun DrawScope.onDraw() {
        drawIntoCanvas { canvas ->
            image.draw(canvas.nativeCanvas)
        }
    }
}
