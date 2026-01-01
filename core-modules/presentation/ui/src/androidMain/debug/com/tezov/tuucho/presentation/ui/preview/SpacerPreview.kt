package com.tezov.tuucho.presentation.ui.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tezov.tuucho.core.presentation.ui.view.createSpacerView
import com.tezov.tuucho.presentation.ui.preview._system.dummyScreenContext

private sealed class SpacerPreviewData {
    data class RowScopeComponent(
        val weight: Float? = null,
        val width: Dp? = null,
        val height: Dp? = null,
    ) : SpacerPreviewData()

    data class ColumnScopeComponent(
        val weight: Float? = null,
        val width: Dp? = null,
        val height: Dp? = null,
    ) : SpacerPreviewData()
}

private class SpacerPreviewDataProvider : PreviewParameterProvider<SpacerPreviewData> {
    override val values = sequenceOf(
        // Row
        SpacerPreviewData.RowScopeComponent(
            weight = 1.0f,
        ),
        SpacerPreviewData.RowScopeComponent(
            weight = 0.25f,
        ),
        SpacerPreviewData.RowScopeComponent(
            width = 112.dp,
        ),
        SpacerPreviewData.RowScopeComponent(
            width = 12.dp,
        ),
        // Column
        SpacerPreviewData.ColumnScopeComponent(
            weight = 1.0f,
        ),
        SpacerPreviewData.ColumnScopeComponent(
            weight = 0.25f,
        ),
        SpacerPreviewData.ColumnScopeComponent(
            height = 112.dp,
        ),
        SpacerPreviewData.ColumnScopeComponent(
            height = 12.dp,
        ),
    )
}

@Composable
private fun SpacerPreviewSquare(
    color: Color = Color.Blue,
    width: Dp = 42.dp,
    height: Dp = 42.dp
) {
    Box(
        modifier = Modifier
            .background(color)
            .width(width)
            .height(height)
    )
}

@Preview(
    showSystemUi = false,
    showBackground = true,
    backgroundColor = 0xD9D9D9,
)
@Composable
private fun SpacerPreviewComponentFromSequence(
    @PreviewParameter(SpacerPreviewDataProvider::class)
    data: SpacerPreviewData,
) {
    when (data) {
        is SpacerPreviewData.RowScopeComponent -> {
            Row(
                Modifier
                    .height(56.dp)
                    .width(256.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SpacerPreviewSquare()
                Spacer(modifier = Modifier.weight(1.0f))
                SpacerPreviewSquare()
                SpacerPreviewComponent(
                    scope = this,
                    weight = data.weight,
                    width = data.width,
                    height = data.height
                )
                SpacerPreviewSquare()
            }
        }

        is SpacerPreviewData.ColumnScopeComponent -> {
            Column(
                Modifier
                    .width(56.dp)
                    .height(256.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SpacerPreviewSquare()
                Spacer(modifier = Modifier.weight(1.0f))
                SpacerPreviewSquare()
                SpacerPreviewComponent(
                    scope = this,
                    weight = data.weight,
                    width = data.width,
                    height = data.height
                )
                SpacerPreviewSquare()
            }
        }
    }
}

@Composable
fun SpacerPreviewComponent(
    scope: Any? = null,
    weight: Float? = null,
    width: Dp? = null,
    height: Dp? = null,
) {
    val view = createSpacerView(screenContext = dummyScreenContext())
    view.ComposeComponent(
        scope = scope,
        weight = weight,
        width = width,
        height = height
    )
}
