package com.tezov.tuucho.core.data.repository.parser.shadower._system

import kotlinx.serialization.json.JsonObject

interface JsonObjectConsumerProtocol {
    suspend fun onNext(jsonObject: JsonObject)
    suspend fun onDone()
}