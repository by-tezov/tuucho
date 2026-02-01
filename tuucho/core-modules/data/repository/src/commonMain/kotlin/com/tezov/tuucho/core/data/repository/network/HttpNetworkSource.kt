package com.tezov.tuucho.core.data.repository.network

import com.tezov.tuucho.core.data.repository.di.NetworkModule
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.test._system.OpenForTest
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.request
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

@OpenForTest
internal class HttpNetworkSource(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val httpClient: HttpClient,
    private val jsonConverter: Json,
    private val config: NetworkModule.Config,
) {
    suspend fun getHealth(
        request: HttpRequest
    ) = coroutineScopes.io.withContext {
        val response = with(config) {
            httpClient.getJson("$baseUrl/$version/$healthEndpoint/${request.url}")
        }
        HttpResponse(
            url = response.request.url.toString(),
            code = response.status.value,
            jsonObject = jsonConverter.decodeFromString(
                deserializer = JsonObject.serializer(),
                string = response.bodyAsText()
            )
        )
    }

    suspend fun getResource(
        request: HttpRequest
    ) = coroutineScopes.io.withContext {
        val response = with(config) {
            httpClient.getJson("$baseUrl/$version/$resourceEndpoint/${request.url}")
        }
        HttpResponse(
            url = response.request.url.toString(),
            code = response.status.value,
            jsonObject = jsonConverter.decodeFromString(
                deserializer = JsonObject.serializer(),
                string = response.bodyAsText()
            )
        )
    }

    suspend fun postSend(
        request: HttpRequest
    ) = coroutineScopes.io.withContext {
        val response = httpClient.postJson(
            url = "${config.baseUrl}/${config.version}/${config.sendEndpoint}/${request.url}",
            jsonObject = request.jsonObject
        )
        HttpResponse(
            url = response.request.url.toString(),
            code = response.status.value,
            jsonObject = jsonConverter.decodeFromString(
                deserializer = JsonObject.serializer(),
                string = response.bodyAsText()
            )
        )
    }
}
