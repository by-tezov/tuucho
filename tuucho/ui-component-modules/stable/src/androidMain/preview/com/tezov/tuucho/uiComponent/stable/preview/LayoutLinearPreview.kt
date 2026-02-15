package com.tezov.tuucho.uiComponent.stable.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tezov.tuucho.core.presentation.ui.preview.DummyScreenContext
import com.tezov.tuucho.uiComponent.stable.domain.jsonSchema.material.layout.LayoutLinearSchema.Style.Value.Orientation
import com.tezov.tuucho.uiComponent.stable.presentation.view.LayoutLinearViewFactory.Companion.createLayoutLinearView

private data class LayoutLinearPreviewData(
    val backgroundColor: Color? = null,
    val orientation: String? = null,
    val fillMaxSize: Boolean? = null,
    val fillMaxWidth: Boolean? = null,
    val contents: List<@Composable (scope: Any?) -> Unit>,
)

private object LayoutLinearPreviewSampleView {
    val labelTitle: @Composable (scope: Any?) -> Unit = {
        LabelPreviewComponent(
            textValue = "Label Title",
            fontColor = Color.Black,
            fontSize = 56.sp,
        )
    }

    fun labelSection(
        color: Color
    ): @Composable (scope: Any?) -> Unit = {
        LabelPreviewComponent(
            textValue = "Label Section",
            fontColor = color,
            fontSize = 32.sp,
        )
    }

    fun field(
        title: String,
        showError: Boolean = false
    ): @Composable (scope: Any?) -> Unit = { scope ->
        FieldPreviewComponent(
            scope = scope,
            showError = showError,
            titleValue = title,
        )
    }

    val button: @Composable (scope: Any?) -> Unit = {
        ButtonPreviewComponent(
            content = {
                LabelPreviewComponent(
                    textValue = "Button",
                    fontColor = Color.Black,
                    fontSize = 18.sp,
                )
            }
        )
    }

    fun spacerWeight(
        value: Float
    ): @Composable (scope: Any?) -> Unit = { scope ->
        SpacerPreviewComponent(
            scope = scope,
            weight = value
        )
    }

    fun spacerHeight(
        value: Dp
    ): @Composable (scope: Any?) -> Unit = { scope ->
        SpacerPreviewComponent(
            scope = scope,
            height = value
        )
    }

    val rowLayout: @Composable (scope: Any?) -> Unit = {
        LayoutLinearPreviewComponent(
            backgroundColor = Color.Gray,
            orientation = Orientation.horizontal,
            fillMaxWidth = true,
            contents = buildList {
                add(button)
                add(spacerWeight(1.0f))
                add(button)
            }
        )
    }
}

private class LayoutLinearPreviewDataProvider : PreviewParameterProvider<LayoutLinearPreviewData> {
    override val values = buildList {
        add(
            LayoutLinearPreviewData(
                backgroundColor = Color.LightGray,
                orientation = Orientation.horizontal,
                contents = buildList {
                    add(LayoutLinearPreviewSampleView.button)
                    add(LayoutLinearPreviewSampleView.spacerWeight(1.0f))
                    add(LayoutLinearPreviewSampleView.button)
                }
            )
        )
        add(
            LayoutLinearPreviewData(
                backgroundColor = Color.LightGray,
                orientation = Orientation.vertical,
                contents = buildList {
                    add(LayoutLinearPreviewSampleView.button)
                    add(LayoutLinearPreviewSampleView.spacerHeight(56.dp))
                    add(LayoutLinearPreviewSampleView.button)
                }
            )
        )
        add(
            LayoutLinearPreviewData(
                backgroundColor = Color.LightGray,
                orientation = Orientation.vertical,
                fillMaxSize = true,
                fillMaxWidth = false,
                contents = buildList {
                    add(LayoutLinearPreviewSampleView.labelTitle)
                    add(LayoutLinearPreviewSampleView.spacerWeight(0.5f))
                    add(LayoutLinearPreviewSampleView.labelSection(Color.Blue))
                    add(LayoutLinearPreviewSampleView.spacerHeight(12.dp))
                    add(LayoutLinearPreviewSampleView.field("field without error"))
                    add(LayoutLinearPreviewSampleView.labelSection(Color.Red))
                    add(LayoutLinearPreviewSampleView.spacerHeight(12.dp))
                    add(LayoutLinearPreviewSampleView.field("field with error", showError = true))
                    add(LayoutLinearPreviewSampleView.spacerWeight(1.0f))
                    add(LayoutLinearPreviewSampleView.rowLayout)
                }
            )
        )
    }.asSequence()
}

@Preview(
    locale = "fr-FR",
    showSystemUi = false,
    showBackground = true,
    backgroundColor = 0xD9D9D9,
)
@Composable
private fun LayoutLinearPreviewComponentFromSequence(
    @PreviewParameter(LayoutLinearPreviewDataProvider::class)
    data: LayoutLinearPreviewData
) {
    LayoutLinearPreviewComponent(
        backgroundColor = data.backgroundColor,
        orientation = data.orientation,
        fillMaxSize = data.fillMaxSize,
        fillMaxWidth = data.fillMaxWidth,
        contents = data.contents
    )
}

@Composable
fun LayoutLinearPreviewComponent(
    backgroundColor: Color? = Color.LightGray,
    orientation: String? = Orientation.vertical,
    fillMaxSize: Boolean? = null,
    fillMaxWidth: Boolean? = null,
    contents: List<@Composable (scope: Any?) -> Unit>,
) {
    val view = createLayoutLinearView(screenContext = DummyScreenContext())
    view.ComposeComponent(
        backgroundColor = backgroundColor,
        orientation = orientation,
        fillMaxSize = fillMaxSize,
        fillMaxWidth = fillMaxWidth,
        contents = contents
    )
}
