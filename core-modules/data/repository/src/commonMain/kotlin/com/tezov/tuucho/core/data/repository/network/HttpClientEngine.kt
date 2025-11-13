package com.tezov.tuucho.core.data.repository.network

import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol.Companion.execute
import io.ktor.client.engine.HttpClientEngineBase
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.client.request.takeFrom
import io.ktor.utils.io.InternalAPI

@OptIn(InternalAPI::class)
internal class HttpClientEngine(
    private val engine: io.ktor.client.engine.HttpClientEngine,
    private val interceptors: List<HttpInterceptor>
) : HttpClientEngineBase("HttpClientEngine") {
    override val dispatcher get() = engine.dispatcher

    override val config get() = engine.config

    override val supportedCapabilities get() = engine.supportedCapabilities

    override suspend fun execute(
        data: HttpRequestData
    ): HttpResponseData {
        val terminal = HttpInterceptor { context, _ ->
            engine.execute(context.builder.build())
        }
        return interceptors.execute(
            HttpInterceptor.Context(
                builder = HttpRequestBuilder().takeFrom(data)
            ),
            terminal
        )
    }

    override fun close() {
        engine.close()
        super.close()
    }
}

internal class HttpClientEngineFactory<out T : HttpClientEngineConfig>(
    private val engineFactory: io.ktor.client.engine.HttpClientEngineFactory<T>,
    private val interceptors: List<HttpInterceptor>
) : io.ktor.client.engine.HttpClientEngineFactory<T> {
    override fun create(
        block: T.() -> Unit
    ): HttpClientEngine = HttpClientEngine(
        engine = engineFactory.create(block),
        interceptors = interceptors,
    )
}
