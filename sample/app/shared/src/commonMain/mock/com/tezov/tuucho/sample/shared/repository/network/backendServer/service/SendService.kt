package com.tezov.tuucho.sample.shared.repository.network.backendServer.service

import com.tezov.tuucho.core.data.repository.di.NetworkModule
import com.tezov.tuucho.sample.shared.repository.network.backendServer.BackendServer
import com.tezov.tuucho.sample.shared.repository.network.backendServer.protocol.GuardProtocol
import com.tezov.tuucho.sample.shared.repository.network.backendServer.protocol.ServiceProtocol
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.http.withCharset
import io.ktor.utils.io.charsets.Charsets

internal class SendService(
    config: NetworkModule.Config,
    private val guards: List<GuardProtocol>,
) : ServiceProtocol {

    private val pattern = Regex("^${config.sendEndpoint}/auth(/.*)?$")

    override fun matches(url: String) = pattern.matches(url)

    override suspend fun allowed(
        version: String,
        request: BackendServer.Request
    ) = guards.all { it.allowed(version, request) }

    override suspend fun process(
        version: String,
        request: BackendServer.Request,
    ) = when (version) {
        "v1" -> BackendServer.Response(
            statusCode = HttpStatusCode.fromValue(200),
            headers = headersOf(
                name = HttpHeaders.ContentType,
                value = ContentType.Application.Json
                    .withCharset(Charsets.UTF_8)
                    .toString()
            ),
            body = """{ "subset" : "form", "all-succeed": true }""".toByteArray(Charsets.UTF_8)
        )

        else -> throw Exception("unknown version $version")
    }
}
