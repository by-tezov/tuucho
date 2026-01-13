package com.tezov.tuucho.core.presentation.ui.screen.protocol

import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.presentation.ui.view.protocol.ViewProtocol

interface ScreenContextProtocol {
    val route: NavigationRoute

    fun addView(
        view: ViewProtocol
    )
}
