package com.tezov.tuucho.core.domain.business.protocol

import kotlinx.serialization.json.JsonObject

interface ServerHealthCheckProtocol {
    suspend fun process(url: String): JsonObject
}