package com.tezov.tuucho.core.ui.renderer._system

import androidx.compose.runtime.Composable
import com.tezov.tuucho.core.domain.protocol.ScreenProtocol

abstract class ComposableScreenProtocol: ScreenProtocol {

    @Composable
    abstract fun show(scope: Any?)
}