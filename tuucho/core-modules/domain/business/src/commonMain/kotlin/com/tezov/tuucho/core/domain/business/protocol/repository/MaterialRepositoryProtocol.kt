package com.tezov.tuucho.core.domain.business.protocol.repository

import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import kotlinx.serialization.json.JsonObject

object MaterialRepositoryProtocol {
    interface RefreshCache {
        suspend fun process(
            url: String
        )
    }

    interface Retrieve {
        suspend fun isValid(
            url: String
        ): Boolean

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
            val route: NavigationRoute.Url,
            val jsonObject: JsonObject,
        )

        suspend fun process(
            route: NavigationRoute.Url,
            types: List<String>,
        ): List<Output>
    }
}
