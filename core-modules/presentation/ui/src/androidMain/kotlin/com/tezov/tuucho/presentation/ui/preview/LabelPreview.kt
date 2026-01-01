package com.tezov.tuucho.presentation.ui.preview

import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.tezov.tuucho.core.presentation.ui.view.createLabelView

sealed class LabelPreviewData {
    data class Component(
        val fontColor: Color,
        val fontSize: TextUnit,
        val text: String,
    ) : LabelPreviewData()

    data object PlaceHolder : LabelPreviewData()
}

class LabelPreviewDataProvider : PreviewParameterProvider<LabelPreviewData> {
    override val values = sequenceOf(
        LabelPreviewData.Component(
            fontColor = Color.Black,
            fontSize = 12.sp,
            text = "Hello world 12sp + black",
        ),
        LabelPreviewData.Component(
            fontColor = Color.Gray,
            fontSize = 16.sp,
            text = "Hello world 16sp + gray",
        ),
        LabelPreviewData.Component(
            fontColor = Color.Red,
            fontSize = 20.sp,
            text = "Hello world 20 + red",
        ),
//        Data.PlaceHolder
    )
}

@Preview(
    locale = "fr-FR",
    showSystemUi = false,
    showBackground = true, backgroundColor = 0xD9D9D9,
)
@Composable
fun LabelPreviewComponent(
    @PreviewParameter(LabelPreviewDataProvider::class)
    data: LabelPreviewData
) {
    val view = remember { createLabelView() }
    when (data) {
        is LabelPreviewData.Component -> {
            val textStyle = LocalTextStyle.current.copy(
                color = data.fontColor,
                fontSize = data.fontSize,
            )

            view.ComposeComponent(
                textStyle = textStyle,
                textValue = data.text
            )
        }

        LabelPreviewData.PlaceHolder -> {
            view.ComposePlaceHolder()
        }
    }
}
