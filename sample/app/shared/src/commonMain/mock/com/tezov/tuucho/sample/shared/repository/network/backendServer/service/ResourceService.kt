package com.tezov.tuucho.sample.shared.repository.network.backendServer.service

import com.tezov.tuucho.core.data.repository.assets.AssetsProtocol
import com.tezov.tuucho.core.domain.business.jsonSchema.material.Shadower.Type
import com.tezov.tuucho.sample.shared.repository.network.backendServer.BackendServer
import com.tezov.tuucho.sample.shared.repository.network.backendServer.protocol.GuardProtocol
import com.tezov.tuucho.sample.shared.repository.network.backendServer.protocol.ServiceProtocol
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.http.withCharset
import io.ktor.utils.io.charsets.Charsets
import kotlinx.coroutines.delay
import okio.buffer
import okio.use
import kotlin.random.Random

internal class ResourceService(
    private val assets: AssetsProtocol,
    private val guards: List<GuardProtocol>,
) : ServiceProtocol {

    private val pattern = Regex("^resource(/.*)?$")

    override fun matches(url: String) = pattern.matches(url)

    override suspend fun allowed(
        version: String,
        request: BackendServer.Request
    ): Boolean {
        val url = request.url.removePrefix("resource/")
        return when {
            url.startsWith("auth") -> {
                guards.all { it.allowed(version, request) }
            }

            url.startsWith("lobby") -> {
                true
            }

            else -> throw Exception("unsupported resource path: $url")
        }
    }

    override suspend fun process(
        version: String,
        request: BackendServer.Request,
    ) = when (version) {
        "v1" -> {
            val jsonString = assets
                .readFile("backend/$version/${request.url}.json")
                .buffer()
                .use { it.readUtf8() }

            if (request.url.endsWith("-${Type.contextual}") || request.url.contains("-${Type.contextual}-")) {
                delay(Random.nextLong(500, 3000))
            }

            BackendServer.Response(
                statusCode = HttpStatusCode.fromValue(200),
                headers = headersOf(
                    name = HttpHeaders.ContentType,
                    value = ContentType.Application.Json
                        .withCharset(Charsets.UTF_8)
                        .toString()
                ),
                body = jsonString
            )
        }

        else -> {
            throw Exception("unknown version $version")
        }
    }
}
