@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.presentation.ui.renderer.view._system

import com.tezov.tuucho.core.domain.business.di.TuuchoKoinComponent
import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import kotlinx.serialization.json.JsonObject

abstract class AbstractViewFactory :
    MatcherViewFactoryProtocol,
    TuuchoKoinComponent {
    abstract suspend fun process(
        route: NavigationRoute.Url,
        componentObject: JsonObject,
    ): ViewProtocol
}
