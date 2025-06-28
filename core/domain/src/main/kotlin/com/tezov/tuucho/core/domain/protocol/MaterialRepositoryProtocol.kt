package com.tezov.tuucho.core.domain.protocol

import kotlinx.serialization.json.JsonObject

interface MaterialRepositoryProtocol {

    suspend fun refreshCache(url: String)

    suspend fun retrieve(url: String): JsonObject

}