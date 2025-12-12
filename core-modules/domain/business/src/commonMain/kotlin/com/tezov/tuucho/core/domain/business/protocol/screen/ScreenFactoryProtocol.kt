package com.tezov.tuucho.core.domain.business.protocol.screen

import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import kotlinx.serialization.json.JsonObject

interface ScreenFactoryProtocol {
    suspend fun create(
        route: NavigationRoute.Url,
        componentObject: JsonObject
    ): ScreenProtocol
}
