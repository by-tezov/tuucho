package com.tezov.tuucho.core.data.repository.network.backendServer

import com.tezov.tuucho.core.data.repository.network.backendServer.protocol.ServiceProtocol
import io.ktor.http.Headers
import io.ktor.http.HttpStatusCode
import io.ktor.http.Url

class BackendServer(
    private val serverUrl: String,
    private val services: List<ServiceProtocol>,
) {
    data class Request(
        val url: String,
        val headers: Headers,
        val body: String?,
    )

    data class Response(
        val statusCode: HttpStatusCode,
        val headers: Headers,
        val body: String?,
    )

    suspend fun process(
        request: Request
    ): Response {
        val parsedUrl = Url(request.url)
        val baseUrl = Url(serverUrl)
        val endpoint = parsedUrl.encodedPath
            .removePrefix(baseUrl.encodedPath)
            .trimStart('/')
        val version = parsedUrl.parameters["version"]
            ?: throw IllegalArgumentException("Missing 'version' query param")
        val url = parsedUrl.parameters["url"]
            ?: throw IllegalArgumentException("Missing 'url' query param")
        return services
            .first { it.url == endpoint }
            .process(version, request.copy(url = url))
    }
}
