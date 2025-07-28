package com.tezov.tuucho.core.data.parser.breaker._system

import com.tezov.tuucho.core.data.parser._system.JsonEntityObjectTree
import kotlinx.serialization.json.JsonObject

fun interface JsonEntityObjectTreeProducerProtocol {
    operator fun invoke(jsonObject: JsonObject): JsonEntityObjectTree
}