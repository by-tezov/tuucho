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

class ModifierChainBuilder(
    var modifier: Modifier
) {
    inline fun <T> ifNotNull(
        value: T?,
        crossinline block: Modifier.(T) -> Modifier
    ): ModifierChainBuilder {
        if (value != null) modifier = block(modifier, value)
        return this
    }

    inline fun ifNull(
        value: Any?,
        crossinline block: Modifier.() -> Modifier
    ): ModifierChainBuilder {
        if (value == null) modifier = block(modifier)
        return this
    }

    inline fun ifTrue(
        condition: Boolean?,
        crossinline block: Modifier.() -> Modifier
    ): ModifierChainBuilder {
        if (condition == true) modifier = block(modifier)
        return this
    }

    inline fun ifFalse(
        condition: Boolean?,
        crossinline block: Modifier.() -> Modifier
    ): ModifierChainBuilder {
        if (condition == false) modifier = block(modifier)
        return this
    }

    infix fun or(
        block: ModifierChainBuilder.() -> Unit
    ): ModifierChainBuilder {
        this.block()
        return this
    }

    infix fun and(
        block: ModifierChainBuilder.() -> Unit
    ): ModifierChainBuilder {
        this.block()
        return this
    }

    infix fun or(
        next: ModifierChainBuilder
    ): ModifierChainBuilder = this

    infix fun and(
        next: ModifierChainBuilder
    ): ModifierChainBuilder = this

    fun build(): Modifier = modifier
}

inline fun Modifier.then(
    block: com.tezov.tuucho.core.presentation.tool.modifier.ModifierChainBuilder.() -> Unit
): Modifier {
    val builder =
        _root_ide_package_.com.tezov.tuucho.core.presentation.tool.modifier.ModifierChainBuilder(
            this
        )
    builder.block()
    return builder.build()
}
