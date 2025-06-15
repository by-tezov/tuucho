package com.tezov.tuucho.core.data.parser._system

import kotlinx.serialization.json.JsonElement

interface Matcher {
    fun accept(path: JsonElementPath, element: JsonElement): Boolean
}