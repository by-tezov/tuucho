//package com.tezov.tuucho.sample.uiExtension.preview
//
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.tooling.preview.PreviewParameter
//import androidx.compose.ui.tooling.preview.PreviewParameterProvider
//import androidx.compose.ui.unit.TextUnit
//import androidx.compose.ui.unit.sp
//import com.tezov.tuucho.core.presentation.ui.preview.DummyScreenContext
//import com.tezov.tuucho.sample.uiExtension.presentation.CustomLabelViewFactory.Companion.createCustomLabelView
//
//private data class CustomLabelPreviewData(
//    val onClick: (() -> Unit)? = null,
//    val text: String? = null,
//    val fontColor: Color? = null,
//    val fontSize: TextUnit? = null,
//    val counter: Int = 0,
//)
//
//private class CustomLabelPreviewDataProvider : PreviewParameterProvider<CustomLabelPreviewData> {
//    override val values = sequenceOf(
//        CustomLabelPreviewData(
//            text = "Hello world 12sp + black",
//            fontColor = Color.LightGray,
//            fontSize = 12.sp,
//        ),
//        CustomLabelPreviewData(
//            text = "Hello world 16sp + gray",
//            fontColor = Color.Green,
//            fontSize = 16.sp,
//        ),
//        CustomLabelPreviewData(
//            text = "Hello world 20 + red",
//            fontColor = Color.Cyan,
//            fontSize = 20.sp,
//        )
//    )
//}
//
//@Preview(
//    showSystemUi = false,
//    showBackground = true,
//    backgroundColor = 0xD9D9D9,
//)
//@Composable
//private fun CustomLabelPreviewComponentFromSequence(
//    @PreviewParameter(CustomLabelPreviewDataProvider::class)
//    data: CustomLabelPreviewData
//) {
//    CustomLabelPreviewComponent(
//        onClick = data.onClick,
//        textValue = data.text,
//        fontColor = data.fontColor,
//        fontSize = data.fontSize,
//        counter = data.counter,
//    )
//}
//
//@Composable
//fun CustomLabelPreviewComponent(
//    onClick: (() -> Unit)? = null,
//    textValue: String? = "Label",
//    fontColor: Color? = Color.LightGray,
//    fontSize: TextUnit? = 16.sp,
//    counter: Int = 0,
//) {
//    val view = createCustomLabelView(screenContext = DummyScreenContext())
//    view.ComposeComponent(
//        onClick = { onClick?.invoke() },
//        textValue = textValue,
//        fontColor = fontColor,
//        fontSize = fontSize,
//        counter = counter,
//    )
//}
