package com.tezov.tuucho.core.domain.protocol

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

sealed interface MaterialRepositoryProtocol

interface RefreshCacheMaterialRepositoryProtocol: MaterialRepositoryProtocol {
    suspend fun process(url: String)
}

interface RetrieveMaterialRepositoryProtocol: MaterialRepositoryProtocol {
    suspend fun process(url: String): JsonElement
}

interface SendDataAndRetrieveMaterialRepositoryProtocol: MaterialRepositoryProtocol {
    suspend fun process(url: String, data: JsonObject): JsonObject?
}
