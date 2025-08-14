package com.tezov.tuucho.core.presentation.ui.animation._system

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed

abstract class ModifierAnimation : Modifier {

    private val modifier = Modifier.composed { animate() }

    @Composable
    abstract fun Modifier.animate(): Modifier

    final override fun all(predicate: (Modifier.Element) -> Boolean) = modifier.all(predicate)

    final override fun any(predicate: (Modifier.Element) -> Boolean) = modifier.any(predicate)

    final override fun <R> foldIn(initial: R, operation: (R, Modifier.Element) -> R): R =
        modifier.foldIn(initial, operation)

    final override fun <R> foldOut(initial: R, operation: (Modifier.Element, R) -> R): R =
        modifier.foldOut(initial, operation)
}