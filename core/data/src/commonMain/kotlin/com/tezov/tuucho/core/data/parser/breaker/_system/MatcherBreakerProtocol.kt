package com.tezov.tuucho.core.data.parser.breaker._system

import com.tezov.tuucho.core.domain._system.JsonElementPath
import kotlinx.serialization.json.JsonElement

interface MatcherBreakerProtocol {
    fun accept(path: JsonElementPath, element: JsonElement): Boolean
}

