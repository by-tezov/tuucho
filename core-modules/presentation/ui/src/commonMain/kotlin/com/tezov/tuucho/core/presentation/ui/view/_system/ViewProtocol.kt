@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.presentation.ui.view._system

import androidx.compose.runtime.Composable
import com.tezov.tuucho.core.presentation.ui.render.protocol.UpdatableProtocol
import com.tezov.tuucho.core.domain.business.protocol.screen.view.ViewProtocol as DomainViewProtocol

interface ViewProtocol : DomainViewProtocol {

    val updatables: List<UpdatableProtocol>

    @Composable
    fun display(
        scope: Any? = null
    )
}
