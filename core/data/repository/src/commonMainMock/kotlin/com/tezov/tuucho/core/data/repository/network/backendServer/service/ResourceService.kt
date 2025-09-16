package com.tezov.tuucho.core.data.repository.network.backendServer.service

import com.tezov.tuucho.core.data.repository.assets.readResourceFile
import com.tezov.tuucho.core.data.repository.network.backendServer.BackendServer
import com.tezov.tuucho.core.data.repository.network.backendServer.protocol.ServiceProtocol
import com.tezov.tuucho.core.domain.business.jsonSchema.material.Shadower.Type
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.http.withCharset
import io.ktor.utils.io.charsets.Charsets
import kotlinx.coroutines.delay
import kotlin.random.Random

class ResourceService : ServiceProtocol {

    override val url = "resource"

    override suspend fun process(
        version: String,
        request: BackendServer.Request,
    ) = when (version) {
        "v1" -> {
            val jsonString = readResourceFile("backend/$version/${request.url}.json")
            if (request.url.endsWith("-${Type.contextual}") || request.url.contains("-${Type.contextual}-")) {
                delay(Random.nextLong(500, 3000))
            }
            BackendServer.Response(
                statusCode = HttpStatusCode.Companion.fromValue(200),
                headers = headersOf(
                    name = HttpHeaders.ContentType,
                    value = ContentType.Application.Json.withCharset(Charsets.UTF_8).toString()
                ),
                body = jsonString
            )
        }

        else -> throw Exception("unknown version $version")
    }
}