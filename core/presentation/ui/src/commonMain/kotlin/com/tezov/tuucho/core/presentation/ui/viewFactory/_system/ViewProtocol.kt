package com.tezov.tuucho.core.presentation.ui.viewFactory._system

import androidx.compose.runtime.Composable
import com.tezov.tuucho.core.domain.business.protocol.ViewProtocol

interface ViewProtocol: ViewProtocol {

    @Composable
    fun display(scope: Any?)
}