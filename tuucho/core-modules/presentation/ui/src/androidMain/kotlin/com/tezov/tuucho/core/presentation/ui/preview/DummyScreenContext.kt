package com.tezov.tuucho.core.presentation.ui.preview

import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.presentation.ui.protocol.ScreenContextProtocol
import com.tezov.tuucho.core.presentation.ui.screen.ScreenContext

object DummyScreenContext {
    operator fun invoke(): ScreenContextProtocol = ScreenContext(
        route = NavigationRoute.Current,
        addViewBlock = {}
    )
}
