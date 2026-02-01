package com.tezov.tuucho.core.data.repository.repository.source

import com.tezov.tuucho.core.data.repository.network.HttpNetworkSource
import com.tezov.tuucho.core.data.repository.network.HttpRequest
import com.tezov.tuucho.core.domain.test._system.OpenForTest
import kotlinx.serialization.json.JsonObject

@OpenForTest
internal class RemoteSource(
    private val httpNetworkSource: HttpNetworkSource,
) {
    suspend fun resource(
        url: String
    ) = httpNetworkSource.getResource(HttpRequest(url)).jsonObject
        ?: throw Exception("failed to retrieve resource at url $url")

    suspend fun send(
        url: String,
        jsonObject: JsonObject
    ) = httpNetworkSource.postSend(HttpRequest(url, jsonObject)).jsonObject

    suspend fun healthCheck(
        url: String
    ): JsonObject {
        val response = httpNetworkSource.getHealth(HttpRequest(url)).jsonObject
            ?: throw Exception("failed to check health at url $url")
        return response
    }
}
