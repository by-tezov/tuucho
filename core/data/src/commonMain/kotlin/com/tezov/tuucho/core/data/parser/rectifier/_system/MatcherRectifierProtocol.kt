package com.tezov.tuucho.core.data.parser.rectifier._system

import com.tezov.tuucho.core.domain._system.JsonElementPath
import kotlinx.serialization.json.JsonElement

interface MatcherRectifierProtocol {
    fun accept(path: JsonElementPath, element: JsonElement): Boolean
}

