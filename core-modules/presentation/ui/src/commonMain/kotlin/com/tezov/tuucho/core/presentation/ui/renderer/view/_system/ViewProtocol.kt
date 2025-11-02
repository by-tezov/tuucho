@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.presentation.ui.renderer.view._system

import androidx.compose.runtime.Composable
import com.tezov.tuucho.core.domain.business.protocol.screen.view.ViewProtocol as DomainViewProtocol

interface ViewProtocol : DomainViewProtocol {
    val children: List<ViewProtocol>?

    @Composable
    fun display(
        scope: Any? = null
    )
}
