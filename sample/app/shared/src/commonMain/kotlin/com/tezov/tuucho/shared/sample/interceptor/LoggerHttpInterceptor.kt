package com.tezov.tuucho.shared.sample.interceptor

import com.tezov.tuucho.core.data.repository.network.HttpInterceptor
import com.tezov.tuucho.shared.sample._system.Logger
import io.ktor.client.request.HttpRequestBuilder

class LoggerHttpInterceptor(
    private val logger: Logger
) : HttpInterceptor.Node {

    override suspend fun intercept(
        builder: HttpRequestBuilder
    ) {
        with(logger) {
            debug("NET") { "${builder.method} - ${builder.url}" }
            builder.headers.entries()
                .takeIf { it.isNotEmpty() }
                ?.let { entries ->
                    debug { "-- Headers --" }
                    entries.forEach { debug { it } }
                    debug { "-------------" }
                }
        }
    }
}
