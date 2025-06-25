package com.tezov.tuucho.core.ui.renderer

import com.tezov.tuucho.core.ui.renderer._system.ComposableScreen
import com.tezov.tuucho.core.ui.renderer._system.Matcher
import kotlinx.serialization.json.JsonObject
import org.koin.core.component.KoinComponent

abstract class Renderer : Matcher, KoinComponent {

    abstract fun process(jsonObject: JsonObject): ComposableScreen

}