package com.tezov.tuucho.core.data.parser.assembler._system

import com.tezov.tuucho.core.domain._system.JsonElementPath
import kotlinx.serialization.json.JsonElement

interface MatcherAssemblerProtocol {
    fun accept(path: JsonElementPath, element: JsonElement): Boolean
}

