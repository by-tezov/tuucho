package com.tezov.tuucho.core.data.repository.network

import com.tezov.tuucho.core.data.repository.di.NetworkModule
import io.ktor.client.plugins.timeout
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

class HttpClient(
    private val config: NetworkModule.Config,
    private val httpClient: io.ktor.client.HttpClient,
    private val jsonConverter: Json,
) {
    suspend fun getJson(
        url: String
    ) = with(config) {
        httpClient.get(url) {
            timeout {
                requestTimeoutMillis = jsonRequestTimeoutMillis
                connectTimeoutMillis = jsonRequestTimeoutMillis
                socketTimeoutMillis = jsonRequestTimeoutMillis
            }
        }
    }

    suspend fun getImage(
        url: String
    ) = with(config) {
        httpClient.get(url) {
            timeout {
                requestTimeoutMillis = imageRequestTimeoutMillis
                connectTimeoutMillis = imageRequestTimeoutMillis
                socketTimeoutMillis = imageRequestTimeoutMillis
            }
        }
    }

    suspend fun postJson(
        url: String,
        jsonObject: JsonObject?
    ) = with(config) {
        httpClient.post(url) {
            contentType(ContentType.Application.Json)
            jsonObject?.let {
                setBody(
                    jsonConverter.encodeToString(
                        serializer = JsonObject.serializer(),
                        value = it
                    )
                )
            }
        }
    }
}
