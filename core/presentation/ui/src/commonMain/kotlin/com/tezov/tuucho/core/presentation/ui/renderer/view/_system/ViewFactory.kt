package com.tezov.tuucho.core.presentation.ui.renderer.view._system

import com.tezov.tuucho.core.presentation.ui.renderer.screen.ScreenIdentifier
import kotlinx.serialization.json.JsonObject
import org.koin.core.component.KoinComponent

abstract class ViewFactory : MatcherViewFactoryProtocol, KoinComponent {

    abstract suspend fun process(
        screenIdentifier: ScreenIdentifier,
        componentObject: JsonObject,
    ): ViewProtocol

}