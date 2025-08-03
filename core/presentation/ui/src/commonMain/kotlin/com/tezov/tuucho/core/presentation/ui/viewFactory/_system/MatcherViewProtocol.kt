package com.tezov.tuucho.core.presentation.ui.viewFactory._system

import kotlinx.serialization.json.JsonObject

interface MatcherViewProtocol {
    fun accept(componentElement: JsonObject): Boolean
}

