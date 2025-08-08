package com.tezov.tuucho.core.presentation.tool.extension

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue

object ExtensionCompositionLocal {

    @Composable
    fun CompositionLocalProvider(
        ancestor: Array<ProvidedValue<*>>,
        parent: @Composable () -> Array<ProvidedValue<*>> = { emptyArray() },
        child: @Composable () -> Array<ProvidedValue<*>> = { emptyArray() },
        content: @Composable () -> Unit,
    ) {
        androidx.compose.runtime.CompositionLocalProvider(values = ancestor) {
            androidx.compose.runtime.CompositionLocalProvider(values = parent()) {
                androidx.compose.runtime.CompositionLocalProvider(
                    values = child(),
                    content = content
                )
            }
        }
    }
}