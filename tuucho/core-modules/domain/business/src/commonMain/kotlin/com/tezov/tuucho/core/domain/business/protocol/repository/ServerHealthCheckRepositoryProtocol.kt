package com.tezov.tuucho.core.domain.business.protocol.repository

import kotlinx.serialization.json.JsonObject

interface ServerHealthCheckRepositoryProtocol {
    suspend fun process(
        url: String
    ): JsonObject
}
