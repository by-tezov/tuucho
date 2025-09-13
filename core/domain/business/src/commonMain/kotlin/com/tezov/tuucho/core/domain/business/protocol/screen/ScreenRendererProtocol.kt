package com.tezov.tuucho.core.domain.business.protocol.screen

import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import kotlinx.serialization.json.JsonObject

interface ScreenRendererProtocol {

    suspend fun process(route: NavigationRoute, componentObject: JsonObject): ScreenProtocol
}