package com.tezov.tuucho.core.domain.business.protocol.repository

import kotlinx.serialization.json.JsonObject

object MaterialRepositoryProtocol {
    interface RefreshCache {
        suspend fun process(
            url: String
        )
    }

    interface Retrieve {
        suspend fun process(
            url: String
        ): JsonObject
    }

    interface SendDataAndRetrieve {
        suspend fun process(
            url: String,
            jsonObject: JsonObject
        ): JsonObject?
    }

    interface Shadower {
        data class Output(
            val type: String,
            val url: String,
            val jsonObject: JsonObject,
        )

        suspend fun process(
            url: String,
            componentObject: JsonObject,
            types: List<String>,
        ): List<Output>
    }
}
