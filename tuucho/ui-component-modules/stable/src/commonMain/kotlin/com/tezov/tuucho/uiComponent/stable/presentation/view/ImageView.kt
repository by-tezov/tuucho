package com.tezov.tuucho.uiComponent.stable.presentation.view

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
                key = Content.Key.image,
            )
        }.contextual
    }.contextual

    override fun getResolvedStatus() = true

    @Composable
    override fun displayComponent(
        scope: Any?
    ) {
        ComposeComponent(

        )
    }

    @Composable
    override fun ComposeComponent(

    ) {
        Text("Soon will be an Image")
    }

    @Composable
    override fun ComposePlaceHolder() {
        displayPlaceholder(null)
    }
}
