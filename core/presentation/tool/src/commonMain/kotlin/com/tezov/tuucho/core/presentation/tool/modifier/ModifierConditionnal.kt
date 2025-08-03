package com.tezov.tuucho.core.presentation.tool.modifier

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed

fun Modifier.thenOnTrue(
    condition: Boolean,
    block: @Composable Modifier.() -> Modifier,
) = thenInternal(condition, onTrue = block)

fun Modifier.thenOnFalse(
    condition: Boolean,
    block: @Composable Modifier.() -> Modifier,
) = thenInternal(condition, onFalse = block)

fun Modifier.then(
    condition: Boolean,
    onTrue: @Composable (Modifier.() -> Modifier),
    onFalse: @Composable (Modifier.() -> Modifier),
) = thenInternal(condition, onTrue, onFalse)

private fun Modifier.thenInternal(
    condition: Boolean,
    onTrue: @Composable (Modifier.() -> Modifier)? = null,
    onFalse: @Composable (Modifier.() -> Modifier)? = null,
) = (if (condition) {
    onTrue?.let { composed { then(Modifier.it()) } }
} else {
    onFalse?.let { composed { then(Modifier.it()) } }
}) ?: this

fun <T : Any> Modifier.thenOnNotNull(
    condition: T?,
    block: @Composable Modifier.(T) -> Modifier,
) = thenInternal(condition, onNotNull = block)

fun <T : Any> Modifier.thenOnNull(
    condition: T?,
    block: @Composable Modifier.() -> Modifier,
) = thenInternal(condition, onNull = block)

fun <T : Any> Modifier.then(
    condition: T?,
    onNotNull: @Composable (Modifier.(T) -> Modifier),
    onNull: @Composable (Modifier.() -> Modifier),
) = thenInternal(condition, onNotNull, onNull)

private fun <T : Any> Modifier.thenInternal(
    condition: T?,
    onNotNull: @Composable (Modifier.(T) -> Modifier)? = null,
    onNull: @Composable (Modifier.() -> Modifier)? = null,
) = (condition?.let {
    onNotNull?.let { composed { then(Modifier.it(condition)) } }
} ?: run {
    onNull?.let { composed { then(Modifier.it()) } }
}) ?: this