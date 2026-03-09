package com.tezov.tuucho.core.presentation.ui.screen

import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.presentation.ui.protocol.ScreenContextProtocol
import com.tezov.tuucho.core.presentation.ui.view.protocol.ViewProtocol

internal class ScreenContext(
    override val route: NavigationRoute,
    private val addViewBlock: suspend (view: ViewProtocol) -> Unit
) : ScreenContextProtocol {

    override suspend fun addView(
        view: ViewProtocol
    ) = addViewBlock(view)
}
