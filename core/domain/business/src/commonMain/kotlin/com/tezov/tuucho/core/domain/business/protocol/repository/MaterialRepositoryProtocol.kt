package com.tezov.tuucho.core.domain.business.protocol.repository

import kotlinx.serialization.json.JsonObject

sealed interface MaterialRepositoryProtocol {

    interface RefreshCache : MaterialRepositoryProtocol {
        suspend fun process(url: String)
    }

    interface Retrieve : MaterialRepositoryProtocol {
        suspend fun process(url: String): JsonObject
    }

    interface SendDataAndRetrieve : MaterialRepositoryProtocol {
        suspend fun process(url: String, jsonObject: JsonObject): JsonObject?
    }

    interface Shadower : MaterialRepositoryProtocol {

        data class Output(
            val type: String,
            val url: String,
            val jsonObject: JsonObject,
        )

        suspend fun process(url: String, materialObject: JsonObject): List<Output>

    }

}

