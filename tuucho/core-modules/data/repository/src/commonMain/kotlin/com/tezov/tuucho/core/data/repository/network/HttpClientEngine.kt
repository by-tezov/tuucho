package com.tezov.tuucho.core.data.repository.network

import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareExecutorProtocol
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareExecutorProtocol.Companion.process
import io.ktor.client.engine.HttpClientEngineBase
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.client.request.takeFrom
import io.ktor.client.utils.EmptyContent
import io.ktor.http.HttpProtocolVersion
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.util.date.GMTDate
import io.ktor.utils.io.InternalAPI
import kotlinx.coroutines.flow.firstOrNull

@OptIn(InternalAPI::class)
internal class HttpClientEngine(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val engine: io.ktor.client.engine.HttpClientEngine,
    private val middlewareExecutor: MiddlewareExecutorProtocol,
    private val interceptors: List<HttpInterceptor>
) : HttpClientEngineBase("HttpClientEngine") {
    override val dispatcher get() = engine.dispatcher

    override val config get() = engine.config

    override val supportedCapabilities get() = engine.supportedCapabilities

    override suspend fun execute(
        data: HttpRequestData
    ): HttpResponseData {
        val terminal = HttpInterceptor { context, _ ->
            emit(engine.execute(context.builder.build()))
        }
        val builder = HttpRequestBuilder().takeFrom(data)
        val response = middlewareExecutor
            .process(
                middlewares = interceptors + terminal,
                context = HttpInterceptor.Context(
                    builder = builder
                )
            )
        return response.firstOrNull() ?: HttpResponseData(
            statusCode = HttpStatusCode.NoContent,
            requestTime = GMTDate(),
            headers = headersOf(),
            version = HttpProtocolVersion.HTTP_1_1,
            body = EmptyContent,
            callContext = builder.executionContext
        )
    }

    override fun close() {
        engine.close()
        super.close()
    }
}

internal class HttpClientEngineFactory<out T : HttpClientEngineConfig>(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val engineFactory: io.ktor.client.engine.HttpClientEngineFactory<T>,
    private val middlewareExecutor: MiddlewareExecutorProtocol,
    private val interceptors: List<HttpInterceptor>
) : io.ktor.client.engine.HttpClientEngineFactory<T> {
    override fun create(
        block: T.() -> Unit
    ): HttpClientEngine = HttpClientEngine(
        coroutineScopes = coroutineScopes,
        engine = engineFactory.create(block),
        middlewareExecutor = middlewareExecutor,
        interceptors = interceptors,
    )
}
