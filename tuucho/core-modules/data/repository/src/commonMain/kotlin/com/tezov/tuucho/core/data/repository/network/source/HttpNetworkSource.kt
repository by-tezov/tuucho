package com.tezov.tuucho.core.data.repository.network.source

import com.tezov.tuucho.core.data.repository.di.NetworkModule
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.request
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

internal class HttpNetworkSource(
    private val httpClient: HttpClient,
    private val jsonConverter: Json,
    private val config: NetworkModule.Config,
) {
    suspend fun getHealth(
        request: HttpRequest
    ): HttpResponse {
        val response = httpClient.get("${config.baseUrl}/${config.version}/${config.healthEndpoint}/${request.url}")
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
        val response = httpClient.get("${config.baseUrl}/${config.version}/${config.resourceEndpoint}/${request.url}")
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
        val response = httpClient.post("${config.baseUrl}/${config.version}/${config.sendEndpoint}/${request.url}") {
            contentType(ContentType.Application.Json)
            request.jsonObject?.let {
                setBody(
                    jsonConverter.encodeToString(
                        serializer = JsonObject.serializer(),
                        value = it
                    )
                )
            }
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
}
