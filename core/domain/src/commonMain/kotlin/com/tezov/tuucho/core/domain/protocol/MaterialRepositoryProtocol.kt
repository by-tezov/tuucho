package com.tezov.tuucho.core.domain.protocol

import kotlinx.serialization.json.JsonElement

sealed interface MaterialRepositoryProtocol

interface RefreshCacheMaterialRepositoryProtocol: MaterialRepositoryProtocol {
    suspend fun refreshCache(url: String)
}

interface RetrieveMaterialRepositoryProtocol: MaterialRepositoryProtocol {
    suspend fun retrieve(url: String): JsonElement
}

interface SendDataMaterialRepositoryProtocol: MaterialRepositoryProtocol {
    suspend fun sendData(url: String, data: JsonElement): JsonElement?
}
