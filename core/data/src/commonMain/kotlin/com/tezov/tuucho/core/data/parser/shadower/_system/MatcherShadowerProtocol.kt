package com.tezov.tuucho.core.data.parser.shadower._system

import com.tezov.tuucho.core.domain._system.JsonElementPath
import kotlinx.serialization.json.JsonElement

interface MatcherShadowerProtocol {
    fun accept(path: JsonElementPath, element: JsonElement): Boolean
}

