package com.tezov.tuucho.core.data.parser.breaker._system

import com.tezov.tuucho.core.data.parser._system.JsonObjectEntityTree
import kotlinx.serialization.json.JsonObject

fun interface JsonObjectEntityTreeFactoryProtocol {
    operator fun invoke(jsonObject: JsonObject): JsonObjectEntityTree
}