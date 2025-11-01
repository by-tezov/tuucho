package com.tezov.tuucho.core.data.repository.network.source

import com.tezov.tuucho.core.data.repository.di.NetworkRepositoryModule
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.request
import io.ktor.http.ContentType
import io.ktor.http.contentType

internal class NetworkHttpRequestSource(
    private val httpClient: HttpClient,
    private val config: NetworkRepositoryModule.Config,
) {

    suspend fun getHealth(url: String): RemoteResponse {
        val response = httpClient.get("${config.baseUrl}/${config.version}/${config.healthEndpoint}/$url")
        return RemoteResponse(
            url = response.request.url.toString(),
            code = response.status.value,
            json = response.bodyAsText()
        )
    }

    suspend fun getResource(url: String): RemoteResponse {
        val response = httpClient.get("${config.baseUrl}/${config.version}/${config.resourceEndpoint}/$url")
        return RemoteResponse(
            url = response.request.url.toString(),
            code = response.status.value,
            json = response.bodyAsText()
        )
    }

    suspend fun postSend(url: String, request: RemoteRequest): RemoteResponse {
        val response = httpClient.post("${config.baseUrl}/${config.version}/${config.sendEndpoint}/$url") {
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