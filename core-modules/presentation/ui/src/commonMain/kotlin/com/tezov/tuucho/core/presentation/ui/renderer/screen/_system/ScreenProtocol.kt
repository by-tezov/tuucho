@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.presentation.ui.renderer.screen._system

import androidx.compose.runtime.Composable
import com.tezov.tuucho.core.domain.business.protocol.screen.ScreenProtocol as DomainScreenProtocol

interface ScreenProtocol : DomainScreenProtocol {
    @Composable
    fun display(
        scope: Any? = null
    )
}
