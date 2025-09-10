package com.tezov.tuucho.core.presentation.ui.transition._system

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity

abstract class AbstractModifierTransition : Modifier {

    private val modifier = Modifier.composed {
        val size = remember { mutableStateOf(Size.Unspecified) }
        val density = LocalDensity.current.density
        onGloballyPositioned {
            size.value = Size(
                width = (it.size.width / density),
                height = (it.size.height / density)
            )

        }.then(animate(size.value))
    }

    @Composable
    abstract fun Modifier.animate(boundaries: Size): Modifier

    final override fun all(predicate: (Modifier.Element) -> Boolean) = modifier.all(predicate)

    final override fun any(predicate: (Modifier.Element) -> Boolean) = modifier.any(predicate)

    final override fun <R> foldIn(initial: R, operation: (R, Modifier.Element) -> R): R =
        modifier.foldIn(initial, operation)

    final override fun <R> foldOut(initial: R, operation: (Modifier.Element, R) -> R): R =
        modifier.foldOut(initial, operation)
}