package com.tezov.tuucho.core.data.repository.parser.shadower._system

import com.tezov.tuucho.core.domain.tool.json.JsonElementPath
import kotlinx.serialization.json.JsonElement

interface MatcherShadowerProtocol {
    fun accept(path: JsonElementPath, element: JsonElement): Boolean
}

