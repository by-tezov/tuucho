package com.tezov.tuucho.core.data.repository.network

import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocolWithReturn
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.HttpResponseData

fun interface HttpExchangeMiddleware : MiddlewareProtocolWithReturn<HttpExchangeMiddleware.Context, HttpResponseData> {
    data class Context(
        val requestBuilder: HttpRequestBuilder,
    )
}
