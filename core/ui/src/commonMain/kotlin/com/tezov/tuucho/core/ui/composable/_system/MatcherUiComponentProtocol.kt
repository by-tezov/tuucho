package com.tezov.tuucho.core.ui.composable._system

import kotlinx.serialization.json.JsonObject

interface MatcherUiComponentProtocol {
    fun accept(componentElement: JsonObject): Boolean
}

