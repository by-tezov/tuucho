package com.tezov.tuucho.core.ui.composable._system

import androidx.compose.runtime.Composable
import com.tezov.tuucho.core.domain.protocol.ScreenProtocol

interface ComposableScreenProtocol: ScreenProtocol {

    @Composable
    fun show(scope: Any?)
}