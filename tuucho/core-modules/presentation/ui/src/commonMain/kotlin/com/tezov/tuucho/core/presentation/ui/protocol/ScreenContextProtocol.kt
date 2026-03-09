package com.tezov.tuucho.core.presentation.ui.protocol

import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.presentation.ui.view.protocol.ViewProtocol

interface ScreenContextProtocol {
    val route: NavigationRoute

    suspend fun addView(
        view: ViewProtocol
    )
}
