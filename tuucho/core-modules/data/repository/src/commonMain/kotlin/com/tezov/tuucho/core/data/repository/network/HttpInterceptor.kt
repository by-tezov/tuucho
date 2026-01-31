package com.tezov.tuucho.core.data.repository.network

import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.HttpResponseData

fun interface HttpInterceptor : MiddlewareProtocol<HttpInterceptor.Context, HttpResponseData> {
    data class Context(
        val requestBuilder: HttpRequestBuilder,
    )
}
