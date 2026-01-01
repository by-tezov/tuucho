package com.tezov.tuucho.core.presentation.ui.screen

import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.presentation.ui.view._system.ViewProtocol

interface ScreenContextProtocol {
    val route: NavigationRoute

    fun addView(
        view: ViewProtocol
    )
}
