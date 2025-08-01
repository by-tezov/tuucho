package com.tezov.tuucho.core.domain.protocol

import kotlinx.serialization.json.JsonObject

sealed interface MaterialRepositoryProtocol

interface RefreshCacheMaterialRepositoryProtocol: MaterialRepositoryProtocol {
    suspend fun process(url: String)
}

interface RetrieveMaterialRepositoryProtocol: MaterialRepositoryProtocol {
    suspend fun process(url: String): JsonObject
}

interface SendDataAndRetrieveMaterialRepositoryProtocol: MaterialRepositoryProtocol {
    suspend fun process(url: String, dataObject: JsonObject): JsonObject?
}
