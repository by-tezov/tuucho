package com.tezov.tuucho.sample.shared.repository.network.backendServer

import com.tezov.tuucho.sample.shared.repository.network.backendServer.protocol.ServiceProtocol
import io.ktor.http.Headers
import io.ktor.http.HttpStatusCode
import io.ktor.http.Url
import io.ktor.http.headersOf

internal class BackendServer(
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
        val segments = endpoint.split('/', limit = 2)
        if (segments.size < 2) {
            throw IllegalArgumentException("Invalid URL format: $endpoint")
        }
        val version = segments[0]
        val url = segments[1]

        val service = services
            .first { it.matches(url) }

        val _request = request.copy(url = url)
        return if (!service.allowed(version, _request)) {
            Response(
                statusCode = HttpStatusCode.Forbidden,
                headers = headersOf(),
                body = null
            )
        } else {
            service.process(version, _request)
        }
    }
}
