package com.tezov.tuucho.sample.shared.repository.network.backendServer.service

import com.tezov.tuucho.core.data.repository.assets.AssetsProtocol
import com.tezov.tuucho.core.data.repository.di.NetworkModule
import com.tezov.tuucho.sample.shared.repository.network.backendServer.BackendServer
import com.tezov.tuucho.sample.shared.repository.network.backendServer.protocol.GuardProtocol
import com.tezov.tuucho.sample.shared.repository.network.backendServer.protocol.ServiceProtocol
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.delay
import okio.buffer
import okio.use
import kotlin.random.Random

internal class ImageService(
    private val config: NetworkModule.Config,
    private val assets: AssetsProtocol,
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
            val response = assets.readImage(
                AssetsProtocol.Request(path = "backend/$version/${request.url}")
            )
            if (response is AssetsProtocol.Response.Failure) {
                throw response.error
            }
            val success = response as AssetsProtocol.Response.Success
            val bytes = success.source.buffer().use { it.readByteArray() }
            delay(Random.nextLong(150, 1500))
            BackendServer.Response(
                statusCode = HttpStatusCode.OK,
                headers = success.headers,
                body = bytes
            )
        }

        else -> {
            throw Exception("unknown version $version")
        }
    }
}
