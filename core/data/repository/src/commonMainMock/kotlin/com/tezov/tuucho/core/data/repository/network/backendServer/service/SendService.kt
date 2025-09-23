package com.tezov.tuucho.core.data.repository.network.backendServer.service

import com.tezov.tuucho.core.data.repository.network.backendServer.BackendServer
import com.tezov.tuucho.core.data.repository.network.backendServer.protocol.ServiceProtocol
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.http.withCharset
import io.ktor.utils.io.charsets.Charsets

class SendService() : ServiceProtocol {

    override val url = "send"

    override suspend fun process(
        version: String,
        request: BackendServer.Request,
    ) = when (version) {
        "v1" -> BackendServer.Response(
            statusCode = HttpStatusCode.Companion.fromValue(200),
            headers = headersOf(
                name = HttpHeaders.ContentType,
                value = ContentType.Application.Json.withCharset(Charsets.UTF_8).toString()
            ),
            body = """{ "type": "all-succeed" }"""
        )

        else -> throw Exception("unknown version $version")
    }
}