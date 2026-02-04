package com.tezov.tuucho.sample.shared.repository.network.backendServer.service

import com.tezov.tuucho.core.data.repository.assets.AssetSourceProtocol
import com.tezov.tuucho.core.data.repository.di.NetworkModule
import com.tezov.tuucho.core.domain.business.jsonSchema.material.Shadower.Type
import com.tezov.tuucho.sample.shared.repository.network.backendServer.BackendServer
import com.tezov.tuucho.sample.shared.repository.network.backendServer.protocol.GuardProtocol
import com.tezov.tuucho.sample.shared.repository.network.backendServer.protocol.ServiceProtocol
import io.ktor.http.HttpStatusCode
import io.ktor.http.headers
import kotlinx.coroutines.delay
import okio.buffer
import kotlin.random.Random

internal class ResourceService(
    private val config: NetworkModule.Config,
    private val assetSource: AssetSourceProtocol,
    private val guards: List<GuardProtocol>,
) : ServiceProtocol {

    private val pattern = Regex("^${config.resourceEndpoint}(/.*)?$")

    override fun matches(url: String) = pattern.matches(url)

    override suspend fun allowed(
        version: String,
        request: BackendServer.Request
    ): Boolean {
        val url = request.url.removePrefix("${config.resourceEndpoint}/")
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
            val (jsonBytes, contentType, contentSize) = assetSource.readFile(
                path = "backend/$version/${request.url}.json"
            ) { content ->
                val bytes = content.source.buffer().readByteArray()
                Triple(bytes, content.contentType, content.size)
            }
            if (request.url.endsWith("-${Type.contextual}") || request.url.contains("-${Type.contextual}-")) {
                delay(Random.nextLong(500, 3000))
            }
            BackendServer.Response(
                statusCode = HttpStatusCode.fromValue(200),
                headers = headers {
                    this["Content-Type"] = contentType
                    this["Content-Length"] = contentSize.toString()
                },
                body = jsonBytes
            )
        }

        else -> {
            throw Exception("unknown version $version")
        }
    }
}
