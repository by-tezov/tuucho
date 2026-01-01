package com.tezov.tuucho.presentation.ui.preview

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.sp
import com.tezov.tuucho.core.presentation.ui.view.createButtonView
import com.tezov.tuucho.presentation.ui.preview._system.dummyScreenContext

private object ButtonPreviewSampleView {
    val labelSmall: @Composable RowScope.() -> Unit = {
        LabelPreviewComponent(
            textValue = "Black Button",
            fontColor = Color.Black,
            fontSize = 12.sp,
        )
    }
    val labelNormal: @Composable RowScope.() -> Unit = {
        LabelPreviewComponent(
            textValue = "Red Button",
            fontColor = Color.Red,
            fontSize = 24.sp,
        )
    }
    val labelDefault: @Composable RowScope.() -> Unit = {
        LabelPreviewComponent(
            textValue = "Blue Button",
            fontColor = Color.Blue,
            fontSize = 32.sp,
        )
    }
}

private data class ButtonPreviewData(
    val onClick: (() -> Unit)? = null,
    val content: @Composable RowScope.() -> Unit,
)

private class ButtonPreviewDataProvider : PreviewParameterProvider<ButtonPreviewData> {
    override val values = buildList {
        add(
            ButtonPreviewData(
                content = ButtonPreviewSampleView.labelSmall,
            )
        )
        add(
            ButtonPreviewData(
                content = ButtonPreviewSampleView.labelNormal,
            )
        )
        add(
            ButtonPreviewData(
                content = ButtonPreviewSampleView.labelDefault,
            )
        )
    }.asSequence()
}

@Preview(
    showSystemUi = false,
    showBackground = true, backgroundColor = 0xD9D9D9,
)
@Composable
private fun ButtonPreviewComponentFromSequence(
    @PreviewParameter(ButtonPreviewDataProvider::class)
    data: ButtonPreviewData
) {
    ButtonPreviewComponent(
        onClick = data.onClick,
        content = data.content
    )
}

@Composable
fun ButtonPreviewComponent(
    onClick: (() -> Unit)? = null,
    content: @Composable RowScope.() -> Unit,
) {
    val view = createButtonView(screenContext = dummyScreenContext())
    view.ComposeComponent(
        onClick = onClick,
        content = content
    )
}
