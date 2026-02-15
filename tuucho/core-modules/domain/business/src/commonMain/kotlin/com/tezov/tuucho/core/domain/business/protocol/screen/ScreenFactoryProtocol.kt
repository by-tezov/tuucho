package com.tezov.tuucho.core.domain.business.protocol.screen

import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute

interface ScreenFactoryProtocol {
    suspend fun create(
        route: NavigationRoute.Url
    ): ScreenProtocol
}
