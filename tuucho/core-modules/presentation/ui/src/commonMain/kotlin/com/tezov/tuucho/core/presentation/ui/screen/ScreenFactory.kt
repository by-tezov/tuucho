package com.tezov.tuucho.core.presentation.ui.screen

import com.tezov.tuucho.core.domain.business._system.koin.TuuchoKoinComponent
import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.screen.ScreenFactoryProtocol
import kotlinx.serialization.json.JsonObject

internal class ScreenFactory(
    private val coroutineScopes: CoroutineScopesProtocol,
) : ScreenFactoryProtocol,
    TuuchoKoinComponent {
    override suspend fun create(
        route: NavigationRoute.Url,
        componentObject: JsonObject
    ) = coroutineScopes.renderer.await {
        Screen(route = route).apply { initialize(componentObject) }
    }
}
