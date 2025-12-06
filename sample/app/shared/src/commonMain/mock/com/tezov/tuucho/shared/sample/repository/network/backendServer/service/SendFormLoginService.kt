package com.tezov.tuucho.shared.sample.repository.network.backendServer.service

import com.tezov.tuucho.shared.sample.repository.network.backendServer.BackendServer
import com.tezov.tuucho.shared.sample.repository.network.backendServer.protocol.ServiceProtocol
import com.tezov.tuucho.shared.sample.repository.network.backendServer.store.LoginTokenStore
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.http.withCharset
import io.ktor.utils.io.charsets.Charsets
import kotlin.random.Random

internal class SendFormLoginService(
    private val loginTokenStore: LoginTokenStore
) : ServiceProtocol {

    private val pattern = Regex("^send/form-from-page-login(/.*)?$")

    override fun matches(url: String) = pattern.matches(url)

    override suspend fun allowed(
        version: String,
        request: BackendServer.Request
    ) = true

    override suspend fun process(
        version: String,
        request: BackendServer.Request,
    ) = when (version) {
        "v1" -> {
            val login = extractLogin(request.body)
                ?: throw IllegalArgumentException("missing login field")
            val token = generateAuthorizationToken()
            loginTokenStore.setToken(login, token)

            BackendServer.Response(
                statusCode = HttpStatusCode.fromValue(200),
                headers = headersOf(
                    name = HttpHeaders.ContentType,
                    value = ContentType.Application.Json
                        .withCharset(Charsets.UTF_8)
                        .toString()
                ),
                body = """{
                  "subset" : "form",
                  "all-succeed": true,
                  "failure-result": "*server-failure",
                  "action": {
                    "before": "store://key-value/save?login-authorization=${token}"
                  }
                }"""
            )
        }

        else -> throw Exception("unknown version $version")
    }

    private fun extractLogin(body: String?): String? {
        if (body == null) return null
        return Regex("\"common@input-field-login\"\\s*:\\s*\"([^\"]+)\"")
            .find(body)
            ?.groupValues?.get(1)
    }

    private fun generateAuthorizationToken(length: Int = 20): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return buildString(length) {
            repeat(length) {
                append(chars[Random.nextInt(chars.length)])
            }
        }
    }
}
