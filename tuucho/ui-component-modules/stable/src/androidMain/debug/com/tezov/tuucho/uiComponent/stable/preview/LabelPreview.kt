package com.tezov.tuucho.uiComponent.stable.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.tezov.tuucho.core.presentation.ui.preview.DummyScreenContext
import com.tezov.tuucho.uiComponent.stable.presentation.view.LabelViewFactory.Companion.createLabelView

private data class LabelPreviewData(
    val text: String? = null,
    val fontColor: Color? = null,
    val fontSize: TextUnit? = null,
)

private class LabelPreviewDataProvider : PreviewParameterProvider<LabelPreviewData> {
    override val values = sequenceOf(
        LabelPreviewData(
            text = "Hello world 12sp + black",
            fontColor = Color.Black,
            fontSize = 12.sp,
        ),
        LabelPreviewData(
            text = "Hello world 16sp + gray",
            fontColor = Color.Gray,
            fontSize = 16.sp,
        ),
        LabelPreviewData(
            text = "Hello world 20 + red",
            fontColor = Color.Red,
            fontSize = 20.sp,
        )
    )
}

@Preview(
    showSystemUi = false,
    showBackground = true,
    backgroundColor = 0xD9D9D9,
)
@Composable
private fun LabelPreviewComponentFromSequence(
    @PreviewParameter(LabelPreviewDataProvider::class)
    data: LabelPreviewData
) {
    LabelPreviewComponent(
        textValue = data.text,
        fontColor = data.fontColor,
        fontSize = data.fontSize,
    )
}

@Composable
fun LabelPreviewComponent(
    textValue: String? = "Label",
    fontColor: Color? = Color.Black,
    fontSize: TextUnit? = 16.sp,
) {
    val view = createLabelView(screenContext = DummyScreenContext())
    view.ComposeComponent(
        textValue = textValue,
        fontColor = fontColor,
        fontSize = fontSize,
    )
}
