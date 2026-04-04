package com.tezov.tuucho.core.presentation.ui.protocol

import androidx.compose.runtime.Composable
import com.tezov.tuucho.core.domain.business.protocol.screen.ScreenProtocol

interface ScreenProtocol : ScreenProtocol {
    @Composable
    fun display(
        scope: Any?
    )
}
