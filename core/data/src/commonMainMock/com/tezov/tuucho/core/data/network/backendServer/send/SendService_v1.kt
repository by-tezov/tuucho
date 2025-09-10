package com.tezov.tuucho.core.data.network.backendServer.send

import com.tezov.tuucho.core.data.network.backendServer.BackendServer
import com.tezov.tuucho.core.data.network.backendServer.protocol.ServiceProtocol
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.http.withCharset
import io.ktor.utils.io.charsets.Charsets

class SendService_v1 : ServiceProtocol {

    override val url = "send"

    override val version = "v1"

    override fun process(request: BackendServer.Request): BackendServer.Response {
        return BackendServer.Response(
            statusCode = HttpStatusCode.Companion.fromValue(200),
            headers = headersOf(
                name = HttpHeaders.ContentType,
                value = ContentType.Application.Json.withCharset(Charsets.UTF_8).toString()
            ),
            body = """{ "isAllSuccess": true }"""
        )
    }
}