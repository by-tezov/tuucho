package com.tezov.tuucho.core.data.repository.assets

import io.ktor.http.Headers
import okio.Source

interface AssetsProtocol {
    data class Request(
        val path: String,
        val headers: Headers = Headers.Empty,
    )

    sealed class Response {
        data class Success(
            val source: Source,
            val headers: Headers
        ) : Response()

        data class Failure(
            val error: Throwable
        ) : Response()
    }

    fun readFile(
        request: Request
    ): Response

    fun readImage(
        request: Request
    ): Response
}
