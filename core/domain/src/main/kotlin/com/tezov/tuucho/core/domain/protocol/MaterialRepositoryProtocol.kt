package com.tezov.tuucho.core.domain.protocol

import kotlinx.serialization.json.JsonElement

interface MaterialRepositoryProtocol {

    suspend fun refreshCache(url: String)

    suspend fun retrieve(url: String): JsonElement

    suspend fun send(url: String, data: JsonElement): JsonElement?
}