package com.tezov.tuucho.core.ui.uiComponentFactory._system

import kotlinx.serialization.json.JsonObject

interface MatcherUiComponentProtocol {
    fun accept(componentElement: JsonObject): Boolean
}

