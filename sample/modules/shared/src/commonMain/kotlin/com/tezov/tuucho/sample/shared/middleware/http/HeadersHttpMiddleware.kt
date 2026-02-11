package com.tezov.tuucho.sample.shared.middleware.http

import com.tezov.tuucho.core.data.repository.network.HttpExchangeMiddleware
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocolWithReturn
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocolWithReturn.Next.Companion.invoke
import com.tezov.tuucho.sample.shared.di.ExceptionHandlerModule
import io.ktor.client.request.HttpResponseData
import kotlinx.coroutines.channels.ProducerScope

class HeadersHttpMiddleware(
    private val config: Config
) : HttpExchangeMiddleware {

    interface Config {
        val headerPlatform: String
    }

    override suspend fun ProducerScope<HttpResponseData>.process(
        context: HttpExchangeMiddleware.Context,
        next: MiddlewareProtocolWithReturn.Next<HttpExchangeMiddleware.Context, HttpResponseData>?
    ) {
        with(context.requestBuilder) {
            headers.append("platform", config.headerPlatform)
            next?.invoke(context)
        }
    }
}
