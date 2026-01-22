package com.tezov.tuucho.sample.shared.repository.network.backendServer.service

import com.tezov.tuucho.core.data.repository.assets.AssetSourceProtocol
import com.tezov.tuucho.core.data.repository.di.NetworkModule
import com.tezov.tuucho.sample.shared.repository.network.backendServer.BackendServer
import com.tezov.tuucho.sample.shared.repository.network.backendServer.protocol.GuardProtocol
import com.tezov.tuucho.sample.shared.repository.network.backendServer.protocol.ServiceProtocol
import io.ktor.http.HttpStatusCode
import io.ktor.http.headers
import kotlinx.coroutines.delay
import okio.buffer
import okio.use
import kotlin.random.Random

internal class ImageService(
    private val config: NetworkModule.Config,
    private val assetSource: AssetSourceProtocol,
    private val guards: List<GuardProtocol>,
) : ServiceProtocol {

    private val pattern = Regex("^${config.imageEndpoint}(/.*)?$")

    override fun matches(url: String) = pattern.matches(url)

    override suspend fun allowed(
        version: String,
        request: BackendServer.Request
    ): Boolean {
        val url = request.url.removePrefix("${config.imageEndpoint}/")
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
            val (bytes, contentType, contentSize) = assetSource.readImage(
                path = "backend/$version/${request.url}"
            ) { content ->
                val data = content.source.buffer().readByteArray()
                Triple(data, content.contentType, content.size)
            }
            delay(Random.nextLong(150, 1500))
            BackendServer.Response(
                statusCode = HttpStatusCode.OK,
                headers = headers {
                    this["Content-Type"] = contentType
                    this["Content-Length"] = contentSize.toString()
                },
                body = bytes
            )
        }

        else -> {
            throw Exception("unknown version $version")
        }
    }
}
