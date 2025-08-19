package com.tezov.tuucho.core.data.network.backendServer

import com.tezov.tuucho.core.data.di.serverUrlEndpoint
import com.tezov.tuucho.core.data.network.backendServer.resource.ResourceService_v1
import com.tezov.tuucho.core.data.network.backendServer.send.SendService_v1
import io.ktor.http.Headers
import io.ktor.http.HttpStatusCode
import io.ktor.http.Url

class BackendServer {

    data class Request(
        val url: String,
        val headers: Headers,
        val body: String?
    )

    data class Response(
        val statusCode: HttpStatusCode,
        val headers: Headers,
        val body: String?
    )

    private val services = listOf(
        ResourceService_v1(),
        SendService_v1()
    )

    fun process(request: Request): Response {
        val parsedUrl = Url(request.url)
        val baseUrl = Url(serverUrlEndpoint())

        val endpoint = parsedUrl.encodedPath
            .removePrefix(baseUrl.encodedPath)
            .trimStart('/')

        val version = parsedUrl.parameters["version"]
            ?: throw IllegalArgumentException("Missing 'version' query param")
        val url = parsedUrl.parameters["url"]
            ?: throw IllegalArgumentException("Missing 'url' query param")

        return services.first { it.url == endpoint && it.version == version }
            .process(request.copy(url = url))
    }

}