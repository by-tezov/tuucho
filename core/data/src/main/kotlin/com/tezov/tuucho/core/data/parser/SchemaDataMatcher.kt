package com.tezov.tuucho.core.data.parser

import com.tezov.tuucho.core.data.parser._system.JsonElementPath
import kotlinx.serialization.json.JsonElement

interface SchemaDataMatcher {
    fun accept(path: JsonElementPath, element: JsonElement): Boolean
}