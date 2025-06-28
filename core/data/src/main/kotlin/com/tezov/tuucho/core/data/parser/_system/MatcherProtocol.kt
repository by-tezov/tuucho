package com.tezov.tuucho.core.data.parser._system

import com.tezov.tuucho.core.domain._system.JsonElementPath
import kotlinx.serialization.json.JsonElement

interface MatcherProtocol {
    fun accept(path: JsonElementPath, element: JsonElement): Boolean
}

