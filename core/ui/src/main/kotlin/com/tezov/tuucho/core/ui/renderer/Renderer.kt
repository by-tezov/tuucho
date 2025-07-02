package com.tezov.tuucho.core.ui.renderer

import com.tezov.tuucho.core.ui.renderer._system.ComposableScreenProtocol
import com.tezov.tuucho.core.ui.renderer._system.MatcherProtocol
import kotlinx.serialization.json.JsonElement
import org.koin.core.component.KoinComponent

abstract class Renderer : MatcherProtocol, KoinComponent {

    abstract fun process(materialElement: JsonElement): ComposableScreenProtocol

}