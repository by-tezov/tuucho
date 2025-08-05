package com.tezov.tuucho.core.domain.business.protocol

import kotlinx.serialization.json.JsonObject

sealed interface MaterialRepositoryProtocol

interface ClearTransientMaterialCacheRepositoryProtocol : MaterialRepositoryProtocol {
    suspend fun process(urlOrigin: String)
}

interface RefreshCacheMaterialRepositoryProtocol : MaterialRepositoryProtocol {
    suspend fun process(url: String)
}

interface RetrieveMaterialRepositoryProtocol : MaterialRepositoryProtocol {
    suspend fun process(url: String): JsonObject
}

interface SendDataAndRetrieveMaterialRepositoryProtocol : MaterialRepositoryProtocol {
    suspend fun process(url: String, jsonObject: JsonObject): JsonObject?
}
