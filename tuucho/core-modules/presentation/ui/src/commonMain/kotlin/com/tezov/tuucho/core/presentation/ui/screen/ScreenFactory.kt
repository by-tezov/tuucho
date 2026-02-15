package com.tezov.tuucho.core.presentation.ui.screen

import com.tezov.tuucho.core.domain.business._system.koin.TuuchoKoinComponent
import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.screen.ScreenFactoryProtocol

internal class ScreenFactory(
    private val coroutineScopes: CoroutineScopesProtocol,
) : ScreenFactoryProtocol,
    TuuchoKoinComponent {
    override suspend fun create(
        route: NavigationRoute.Url
    ) = Screen(route = route).apply { createViews() }
}
