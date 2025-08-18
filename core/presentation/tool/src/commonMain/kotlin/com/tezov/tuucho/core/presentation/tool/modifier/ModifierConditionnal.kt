package com.tezov.tuucho.core.presentation.tool.modifier

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import com.tezov.tuucho.core.domain.tool.extension.ExtensionBoolean.isTrue

fun Modifier.thenOnTrue(
    condition: Boolean?,
    block: @Composable Modifier.() -> Modifier,
) = thenInternal(condition, onTrue = block)

fun Modifier.thenOnFalse(
    condition: Boolean?,
    block: @Composable Modifier.() -> Modifier,
) = thenInternal(condition, onFalse = block)

private fun Modifier.thenInternal(
    condition: Boolean?,
    onTrue: @Composable (Modifier.() -> Modifier)? = null,
    onFalse: @Composable (Modifier.() -> Modifier)? = null,
) = (if (condition.isTrue) {
    onTrue?.let { composed { then(it()) } }
} else {
    onFalse?.let { composed { then(it()) } }
}) ?: this

fun <T : Any> Modifier.thenOnNotNull(
    condition: T?,
    block: @Composable Modifier.(T) -> Modifier,
) = thenInternal(condition, onNotNull = block)

fun Modifier.thenOnNotNull(
    condition: Modifier?
) = thenInternal(condition, onNotNull = { then(it) })

fun <T : Any> Modifier.thenOnNull(
    condition: T?,
    block: @Composable Modifier.() -> Modifier,
) = thenInternal(condition, onNull = block)

private fun <T : Any> Modifier.thenInternal(
    condition: T?,
    onNotNull: @Composable (Modifier.(T) -> Modifier)? = null,
    onNull: @Composable (Modifier.() -> Modifier)? = null,
) = (condition?.let {
    onNotNull?.let { composed { then(it(condition)) } }
} ?: run {
    onNull?.let { composed { then(it()) } }
}) ?: this

class ModifierConditional(
    private val apply: (Modifier) -> Modifier,
    private val isApplied: Boolean,
) {
    fun applyTo(modifier: Modifier) = apply(modifier)

    infix fun or(other: ModifierConditional) = if (isApplied) this else other

    infix fun or(other: Modifier.() -> Unit) = if (isApplied) {
        this
    } else {
        val modifier = Modifier.apply(other)
        ModifierConditional({ modifier }, modifier !== Modifier)
    }

    infix fun and(other: ModifierConditional) = ModifierConditional(
        apply = { modifier ->
            val firstApplied = applyTo(modifier)
            other.applyTo(firstApplied)
        },
        isApplied = isApplied && other.isApplied
    )

    infix fun and(other: Modifier.() -> Unit) = if (!isApplied) {
        Empty
    } else {
        val modifier = Modifier.apply(other)
        if (modifier !== Modifier) {
            ModifierConditional(
                apply = { base -> modifier.then(applyTo(base)) },
                isApplied = true
            )
        } else {
            this
        }
    }

    companion object {
        val Empty = ModifierConditional({ it }, false)
    }
}

fun Modifier.then(block: Modifier.() -> ModifierConditional) = block().applyTo(this)

fun onTrue(condition: Boolean?, block: Modifier.() -> Modifier) = if (condition == true) {
    ModifierConditional({ it.block() }, true)
} else {
    ModifierConditional.Empty
}

fun onFalse(condition: Boolean?, block: Modifier.() -> Modifier) = if (condition == false) {
    ModifierConditional({ it.block() }, true)
} else {
    ModifierConditional.Empty
}

fun <T : Any> onNotNull(value: T?, block: Modifier.(T) -> Modifier) = value?.let {
    ModifierConditional({ it.block(value) }, true)
} ?: ModifierConditional.Empty

fun <T : Any> onNull(value: T?, block: Modifier.() -> Modifier) = if (value == null) {
    ModifierConditional({ it.block() }, true)
} else {
    ModifierConditional.Empty
}