package com.tezov.tuucho.core.ui.renderer._system

import kotlinx.serialization.json.JsonElement

interface MatcherProtocol {
    fun accept(element: JsonElement): Boolean
}

