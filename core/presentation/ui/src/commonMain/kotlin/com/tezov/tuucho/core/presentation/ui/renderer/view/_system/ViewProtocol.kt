package com.tezov.tuucho.core.presentation.ui.renderer.view._system

import androidx.compose.runtime.Composable
import com.tezov.tuucho.core.presentation.ui.renderer.screen.ScreenIdentifier
import com.tezov.tuucho.core.domain.business.protocol.screen.view.ViewProtocol as DomainViewProtocol

typealias ViewIdentifierFactory = (screenIdentifier: ScreenIdentifier) -> ViewIdentifier

interface ViewProtocol : DomainViewProtocol {

    val children: List<ViewProtocol>?

    @Composable
    fun display(scope: Any? = null)
}