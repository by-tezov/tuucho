package com.tezov.tuucho.core.ui.uiComponentFactory._system

import androidx.compose.runtime.Composable
import com.tezov.tuucho.core.domain.protocol.ViewProtocol

interface ViewProtocol: ViewProtocol {

    @Composable
    fun display(scope: Any?)
}