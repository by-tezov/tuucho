package com.tezov.tuucho.uiComponent.stable.preview

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.PlatformContext
import com.tezov.tuucho.core.presentation.ui._system.LocalTuuchoKoin
import com.tezov.tuucho.core.presentation.ui.preview.DummyImage
import com.tezov.tuucho.core.presentation.ui.preview.DummyKoin
import com.tezov.tuucho.core.presentation.ui.preview.DummyScreenContext
import com.tezov.tuucho.core.presentation.ui.render.projection.Image
import com.tezov.tuucho.uiComponent.stable.domain.jsonSchema.material.ImageSchema.Style
import com.tezov.tuucho.uiComponent.stable.presentation.view.ImageViewFactory.Companion.createImageView
import org.koin.dsl.module

private data class ImagePreviewData(
    val width: Dp? = null,
    val height: Dp? = null,
    val shape: String? = null,
    val padding: Dp? = null,
    val alpha: Float? = null,
    val backgroundColor: Color? = null,
    val tintColor: Color? = null,
    val image: Image? = null,
    val description: String? = null,
)

private class ImagePreviewDataProvider : PreviewParameterProvider<ImagePreviewData> {
    override val values = sequenceOf(
        ImagePreviewData(
            width = 128.dp,
            height = 128.dp,
            shape = Style.Value.Shape.rounded,
            image = DummyImage(color = Color.Black),
        ),
        ImagePreviewData(
            width = 128.dp,
            height = 128.dp,
            shape = Style.Value.Shape.roundedSquare,
            image = DummyImage(color = Color.Black),
        ),
        ImagePreviewData(
            width = 128.dp,
            height = 128.dp,
            shape = Style.Value.Shape.rounded,
            padding = 4.dp,
            backgroundColor = Color.Gray,
            image = DummyImage(color = Color.Black),
        ),
        ImagePreviewData(
            width = 128.dp,
            height = 128.dp,
            shape = Style.Value.Shape.roundedSquare,
            padding = 4.dp,
            backgroundColor = Color.Gray,
            image = DummyImage(color = Color.Black),
        ),
        ImagePreviewData(
            width = 96.dp,
            height = 96.dp,
            shape = Style.Value.Shape.rounded,
            padding = 4.dp,
            backgroundColor = Color.Gray,
            alpha = 0.5f,
            image = DummyImage(color = Color.Black),
        ),
        ImagePreviewData(
            width = 96.dp,
            height = 96.dp,
            shape = Style.Value.Shape.rounded,
            padding = 4.dp,
            backgroundColor = Color.Gray,
            tintColor = Color.Red,
            image = DummyImage(color = Color.Black),
        ),
        ImagePreviewData(
            width = 96.dp,
            height = 96.dp,
            shape = Style.Value.Shape.roundedSquare,
            padding = 4.dp,
            backgroundColor = Color.Gray,
            tintColor = Color.Red,
            image = DummyImage(color = Color.White),
        )
    )
}

@Preview(
    showSystemUi = false,
    showBackground = true,
    backgroundColor = 0xD9D9D9,
)
@Composable
private fun ImagePreviewComponentFromSequence(
    @PreviewParameter(ImagePreviewDataProvider::class)
    data: ImagePreviewData
) {
    ImagePreviewComponent(
        width = data.width,
        height = data.height,
        shape = data.shape,
        padding = data.padding,
        alpha = data.alpha,
        backgroundColor = data.backgroundColor,
        tintColor = data.tintColor,
        image = data.image,
        description = data.description
    )
}

@Composable
fun ImagePreviewComponent(
    width: Dp? = 128.dp,
    height: Dp? = 128.dp,
    shape: String? = Style.Value.Shape.roundedSquare,
    padding: Dp? = 4.dp,
    alpha: Float? = 1.0f,
    backgroundColor: Color? = Color.LightGray,
    tintColor: Color? = Color.Transparent,
    image: Image? = null,
    description: String? = "description",
) {
    val context = LocalContext.current
    CompositionLocalProvider(
        LocalTuuchoKoin provides DummyKoin(module { single<PlatformContext> { context } })
    ) {
        val view = createImageView(screenContext = DummyScreenContext())
        view.ComposeComponent(
            width = width,
            height = height,
            shape = shape,
            padding = padding,
            alpha = alpha,
            backgroundColor = backgroundColor,
            tintColor = tintColor,
            image = image,
            description = description
        )
    }
}
