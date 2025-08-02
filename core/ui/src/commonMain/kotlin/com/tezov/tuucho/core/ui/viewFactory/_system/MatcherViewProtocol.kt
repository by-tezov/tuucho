package com.tezov.tuucho.core.ui.viewFactory._system

import kotlinx.serialization.json.JsonObject

interface MatcherViewProtocol {
    fun accept(componentElement: JsonObject): Boolean
}

