package com.tezov.tuucho.core.presentation.ui.preview

import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.presentation.ui.screen.ScreenContext
import com.tezov.tuucho.core.presentation.ui.screen.protocol.ScreenContextProtocol

object DummyScreenContext {
    operator fun invoke(): ScreenContextProtocol = ScreenContext(
        route = NavigationRoute.Current,
        addViewBlock = {}
    )
}
