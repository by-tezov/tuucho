package com.tezov.tuucho.core.data.network

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.request
import io.ktor.http.ContentType
import io.ktor.http.contentType

class NetworkHttpRequest(
    private val httpClient: HttpClient,
    private val baseUrl: String
)  {

    suspend fun retrieve(url: String): RemoteResponse {
        val response = httpClient.get("$baseUrl/resource") {
            parameter("version", "v1")
            parameter("url", url)
        }
        return RemoteResponse(
            url = response.request.url.toString(),
            code = response.status.value,
            json = response.bodyAsText()
        )
    }

    suspend fun send(url: String, request: RemoteRequest): RemoteResponse {
        val response = httpClient.post("$baseUrl/send") {
            parameter("version", "v1")
            parameter("url", url)
            contentType(ContentType.Application.Json)
            setBody(request.json)
        }
        return RemoteResponse(
            url = response.request.url.toString(),
            code = response.status.value,
            json = response.bodyAsText()
        )
    }
}