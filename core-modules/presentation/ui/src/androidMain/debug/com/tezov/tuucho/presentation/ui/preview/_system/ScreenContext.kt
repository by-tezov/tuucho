package com.tezov.tuucho.presentation.ui.preview._system

import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.presentation.ui.screen.ScreenContext
import com.tezov.tuucho.core.presentation.ui.screen.protocol.ScreenContextProtocol

fun dummyScreenContext(): ScreenContextProtocol = ScreenContext(
    route = NavigationRoute.Current,
    addViewBlock = {}
)
