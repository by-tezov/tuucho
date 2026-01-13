package com.tezov.tuucho.uiComponent.stable.preview

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.tezov.tuucho.presentation.ui.preview._system.dummyScreenContext
import com.tezov.tuucho.presentation.ui.preview._system.storageOf
import com.tezov.tuucho.uiComponent.stable.presentation.view.createFieldView

private data class FieldPreviewData(
    val fieldValue: String? = null,
    val showError: Boolean = false,
    val titleValue: String? = null,
    val placeholderValue: String? = null,
    val supportingTexts: List<String>? = null,
    val messageErrorExtra: String? = null
)

private class FieldPreviewDataProvider : PreviewParameterProvider<FieldPreviewData> {
    override val values = sequenceOf(
        FieldPreviewData(
            titleValue = "title"
        ),
        FieldPreviewData(
            placeholderValue = "placeholder",
        ),
        FieldPreviewData(
            titleValue = "title",
            placeholderValue = "placeholder",
        ),
        FieldPreviewData(
            fieldValue = "---- value ----",
            titleValue = "title",
            placeholderValue = "placeholder",
        ),
        FieldPreviewData(
            fieldValue = "---- value ----",
            showError = true,
            titleValue = "title",
            supportingTexts = listOf("error message 1", "error message 2"),
        ),
        FieldPreviewData(
            fieldValue = "---- incorrect value ----",
            showError = true,
            titleValue = "title",
            messageErrorExtra = "extra message error",
        ),
        FieldPreviewData(
            fieldValue = "---- incorrect value ----",
            showError = true,
            titleValue = "title",
            supportingTexts = listOf("error message 1", "error message 2"),
            messageErrorExtra = "extra message error",
        ),
    )
}

@Preview(
    showSystemUi = false,
    showBackground = true,
    backgroundColor = 0xD9D9D9,
)
@Composable
private fun FieldPreviewComponentFromSequence(
    @PreviewParameter(FieldPreviewDataProvider::class)
    data: FieldPreviewData
) {
    FieldPreviewComponent(
        fieldValue = data.fieldValue,
        showError = data.showError,
        titleValue = data.titleValue,
        placeholderValue = data.placeholderValue,
        supportingTexts = data.supportingTexts,
        messageErrorExtra = data.messageErrorExtra
    )
}

@Composable
fun FieldPreviewComponent(
    fieldValue: String? = "value",
    showError: Boolean = false,
    titleValue: String? = "title",
    placeholderValue: String? = "placeholder",
    supportingTexts: List<String>? = listOf("error message 1"),
    messageErrorExtra: String? = null,
) {
    val view = createFieldView(screenContext = dummyScreenContext())
    view.ComposeComponent(
        fieldValue = storageOf(fieldValue),
        showError = remember { mutableStateOf(showError) },
        titleValue = titleValue,
        placeholderValue = placeholderValue,
        supportingTexts = supportingTexts,
        messageErrorExtra = messageErrorExtra
    )
}
