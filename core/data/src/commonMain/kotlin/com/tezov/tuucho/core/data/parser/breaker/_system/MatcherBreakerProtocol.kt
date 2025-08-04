package com.tezov.tuucho.core.data.parser.breaker._system

import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import kotlinx.serialization.json.JsonElement

interface MatcherBreakerProtocol {
    fun accept(path: JsonElementPath, element: JsonElement): Boolean
}

