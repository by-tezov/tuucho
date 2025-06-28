package com.tezov.tuucho.core.ui.renderer._system

import kotlinx.serialization.json.JsonObject

interface MatcherProtocol {
    fun accept( jsonObject: JsonObject): Boolean
}

