package com.tezov.tuucho.core.presentation.ui.renderer.view._system

import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import kotlinx.serialization.json.JsonObject
import org.koin.core.component.KoinComponent

abstract class AbstractViewFactory : MatcherViewFactoryProtocol, KoinComponent {

    abstract suspend fun process(
        route: NavigationRoute,
        componentObject: JsonObject,
    ): ViewProtocol

}