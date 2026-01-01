package com.tezov.tuucho.core.presentation.ui.screen

import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.presentation.ui.screen.protocol.ScreenContextProtocol
import com.tezov.tuucho.core.presentation.ui.view.protocol.ViewProtocol

internal class ScreenContext(
    override val route: NavigationRoute,
    private val addViewBlock: (view: ViewProtocol) -> Unit
) : ScreenContextProtocol {
    override fun addView(
        view: ViewProtocol
    ) = addViewBlock(view)
}

// Needed for Preview
fun dummyScreenContext(): ScreenContextProtocol = ScreenContext(
    route = NavigationRoute.Current,
    addViewBlock = {}
)
