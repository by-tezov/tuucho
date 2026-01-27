package com.tezov.tuucho.core.data.repository.network

import com.tezov.tuucho.core.data.repository.di.NetworkModule
import com.tezov.tuucho.core.domain.test._system.OpenForTest
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.request
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

@OpenForTest
internal class HttpNetworkSource(
    private val httpClient: HttpClient,
    private val jsonConverter: Json,
    private val config: NetworkModule.Config,
) {
    suspend fun getHealth(
        request: HttpRequest
    ): HttpResponse {
        val response = with(config) {
            httpClient.getJson("${baseUrl}/${version}/${healthEndpoint}/${request.url}")
        }
        return HttpResponse(
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
    ): HttpResponse {
        val response = with(config) {
            httpClient.getJson("${baseUrl}/${version}/${resourceEndpoint}/${request.url}")
        }
        return HttpResponse(
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
    ): HttpResponse {
        val response = httpClient.postJson(
            url = "${config.baseUrl}/${config.version}/${config.sendEndpoint}/${request.url}",
            jsonObject = request.jsonObject
        )
        return HttpResponse(
            url = response.request.url.toString(),
            code = response.status.value,
            jsonObject = jsonConverter.decodeFromString(
                deserializer = JsonObject.serializer(),
                string = response.bodyAsText()
            )
        )
    }
}
