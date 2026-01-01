package com.tezov.tuucho.presentation.ui.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tezov.tuucho.core.presentation.ui.view.createSpacerView

sealed class SpacerPreviewData {
    data class RowComponent(
        val weight: Float,
    ) : SpacerPreviewData()

    data class ColumnComponent(
        val weight: Float,
    ) : SpacerPreviewData()

    data class DefaultComponent(
        val width: Dp? = null,
        val height: Dp? = null,
    ) : SpacerPreviewData()
}

class SpacerPreviewDataProvider : PreviewParameterProvider<SpacerPreviewData> {
    override val values = sequenceOf(
        // Row
        SpacerPreviewData.RowComponent(
            weight = 1.0f,
        ),
        SpacerPreviewData.RowComponent(
            weight = 0.25f,
        ),
        SpacerPreviewData.DefaultComponent(
            width = 24.dp,
        ),
        SpacerPreviewData.DefaultComponent(
            width = 56.dp,
        ),

        // Column
        SpacerPreviewData.ColumnComponent(
            weight = 1.0f,
        ),
        SpacerPreviewData.ColumnComponent(
            weight = 0.25f,
        ),
        SpacerPreviewData.DefaultComponent(
            height = 24.dp,
        ),
        SpacerPreviewData.DefaultComponent(
            height = 56.dp,
        ),
    )
}

@Preview(
    locale = "fr-FR",
    showSystemUi = false,
    showBackground = true, backgroundColor = 0xD9D9D9,
)
@Composable
fun SpacerPreviewComponent(
    @PreviewParameter(SpacerPreviewDataProvider::class)
    data: SpacerPreviewData
) {
    val view = remember { createSpacerView() }
    when (data) {
        is SpacerPreviewData.RowComponent -> {
            Row(
                Modifier
                    .height(56.dp)
                    .width(256.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Square()
                Spacer(modifier = Modifier.weight(1.0f))
                Square()
                view.ComposeRowComponent(
                    scope = this,
                    weight = data.weight
                )
                Square()
            }
        }

        is SpacerPreviewData.ColumnComponent -> {
            Column(
                Modifier
                    .width(56.dp)
                    .height(256.dp),
                horizontalAlignment = Alignment.CenterHorizontally

            ) {
                Square()
                Spacer(modifier = Modifier.weight(1.0f))
                Square()
                view.ComposeColumnComponent(
                    scope = this,
                    weight = data.weight
                )
                Square()
            }
        }

        is SpacerPreviewData.DefaultComponent -> {
            if(data.width != null) {
                Row {
                    Square()
                    view.ComposeDefaultComponent(
                        width = data.width,
                        height = data.height
                    )
                    Square()
                }
            }
            else if(data.height != null) {
                Column() {
                    Square()
                    view.ComposeDefaultComponent(
                        width = data.width,
                        height = data.height
                    )
                    Square()
                }
            }
        }

        LabelPreviewData.PlaceHolder -> {
            view.ComposePlaceHolder()
        }
    }
}

@Composable
private fun Square(
    color: Color = Color.Blue,
    width: Dp = 42.dp,
    height: Dp = 46.dp
) {
    Box(
        modifier = Modifier
            .background(color)
            .width(width)
            .height(height)
    )
}
