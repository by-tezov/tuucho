package com.tezov.tuucho.core.domain.repository

import kotlinx.serialization.json.JsonObject

interface MaterialRepository {

    suspend fun refreshCache(url: String)

    suspend fun retrieve(url: String): JsonObject

}