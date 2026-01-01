package com.tezov.tuucho.presentation.ui.preview

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.tezov.tuucho.core.presentation.ui.view.createFieldView

sealed class FieldPreviewData {
    data class Component(
        val fieldValue: MutableState<String> = mutableStateOf(""),
        val showError: MutableState<Boolean> = mutableStateOf(false),
        val titleValue: String? = null,
        val placeholderValue: String? = null,
        val supportingTexts: List<String>? = null,
        val messageErrorExtra: String? = null
    ) : FieldPreviewData()

    data object PlaceHolder : FieldPreviewData()
}

class FieldPreviewDataProvider : PreviewParameterProvider<FieldPreviewData> {
    override val values = sequenceOf(
        FieldPreviewData.Component(
            titleValue = "title"
        ),
        FieldPreviewData.Component(
            placeholderValue = "placeholder",
        ),
        FieldPreviewData.Component(
            titleValue = "title",
            placeholderValue = "placeholder",
        ),
        FieldPreviewData.Component(
            fieldValue = mutableStateOf("---- value ----"),
            titleValue = "title",
            placeholderValue = "placeholder",
        ),
        FieldPreviewData.Component(
            fieldValue = mutableStateOf("---- value ----"),
            showError = mutableStateOf(true),
            titleValue = "title",
            supportingTexts = listOf("error message 1", "error message 2"),
        ),
        FieldPreviewData.Component(
            fieldValue = mutableStateOf("---- incorrect value ----"),
            showError = mutableStateOf(true),
            titleValue = "title",
            messageErrorExtra = "extra message error",
        ),
        FieldPreviewData.Component(
            fieldValue = mutableStateOf("---- incorrect value ----"),
            showError = mutableStateOf(true),
            titleValue = "title",
            supportingTexts = listOf("error message 1", "error message 2"),
            messageErrorExtra = "extra message error",
        ),
    )
}

@Preview(
    locale = "fr-FR",
    showSystemUi = false,
    showBackground = true, backgroundColor = 0xD9D9D9,
)
@Composable
fun FieldPreviewComponent(
    @PreviewParameter(FieldPreviewDataProvider::class)
    data: FieldPreviewData
) {
    val view = remember { createFieldView() }
    when (data) {
        is FieldPreviewData.Component -> {
            view.ComposeComponent(
                fieldValue = data.fieldValue,
                showError = data.showError,
                titleValue = data.titleValue,
                placeholderValue = data.placeholderValue,
                supportingTexts = data.supportingTexts,
                messageErrorExtra = data.messageErrorExtra
            )
        }

        FieldPreviewData.PlaceHolder -> {
            view.ComposePlaceHolder()
        }
    }
}
