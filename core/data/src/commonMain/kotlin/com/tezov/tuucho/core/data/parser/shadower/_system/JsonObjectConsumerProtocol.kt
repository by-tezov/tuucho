package com.tezov.tuucho.core.data.parser.shadower._system

import kotlinx.serialization.json.JsonObject

fun interface JsonObjectConsumerProtocol {
    operator fun invoke(jsonObject: JsonObject)
}