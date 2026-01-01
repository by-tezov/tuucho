package com.tezov.tuucho.presentation.ui.preview

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.tezov.tuucho.core.presentation.ui.view.createButtonView

sealed class ButtonPreviewData {
    data class Component(
        val onClick: () -> Unit = {},
        val content: @Composable RowScope.() -> Unit,
    ) : ButtonPreviewData()

    data object PlaceHolder : ButtonPreviewData()
}

class ButtonPreviewDataProvider : PreviewParameterProvider<ButtonPreviewData> {
    override val values = buildList {
        LabelPreviewDataProvider().values.forEach { labelData ->
            add(
                ButtonPreviewData.Component(
                    content = { LabelPreviewComponent(data = labelData) },
                )
            )
        }
//        Data.PlaceHolder
    }.asSequence()
}

@Preview(
    locale = "fr-FR",
    showSystemUi = false,
    showBackground = true, backgroundColor = 0xD9D9D9,
)
@Composable
fun ButtonPreviewComponent(
    @PreviewParameter(ButtonPreviewDataProvider::class)
    data: ButtonPreviewData
) {
    val view = remember { createButtonView() }
    when (data) {
        is ButtonPreviewData.Component -> {
            view.ComposeComponent(
                onClick = data.onClick,
                content = data.content
            )
        }

        ButtonPreviewData.PlaceHolder -> {
            view.ComposePlaceHolder()
        }
    }
}
