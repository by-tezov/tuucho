package com.tezov.tuucho.core.ui.renderer._system

import androidx.compose.runtime.Composable
import com.tezov.tuucho.core.domain.protocol.ScreenProtocol

abstract class ComposableScreenProtocol: ScreenProtocol {

    final override fun display() {
        throw NotImplementedError("cast to ComposableScreen and use Composable Any.show() function")
    }

    @Composable
    abstract fun show(scope: Any?)
}