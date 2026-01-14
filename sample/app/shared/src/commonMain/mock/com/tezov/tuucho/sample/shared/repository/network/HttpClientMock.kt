package com.tezov.tuucho.sample.shared.repository.network

import com.tezov.tuucho.sample.shared.repository.network.backendServer.BackendServer
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.HttpClientEngineBase
import io.ktor.client.engine.HttpClientEngineCapability
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.plugins.HttpTimeoutCapability
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.http.HttpProtocolVersion
import io.ktor.http.content.OutgoingContent
import io.ktor.util.date.GMTDate
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.InternalAPI
import kotlinx.coroutines.Job

class HttpClientMockConfig : HttpClientEngineConfig()

@OptIn(InternalAPI::class)
internal class HttpClientMockEngine(
    override val config: HttpClientMockConfig,
    private val backendServer: BackendServer,
) : HttpClientEngineBase("HttpMockEngine") {
    private val callContext = coroutineContext + Job()

    override val supportedCapabilities: Set<HttpClientEngineCapability<*>> = setOf(
        HttpTimeoutCapability
    )

    override suspend fun execute(
        data: HttpRequestData
    ): HttpResponseData {
        val response = backendServer.process(
            BackendServer.Request(
                url = data.url.toString(),
                headers = data.headers,
                body = (data.body as? OutgoingContent.ByteArrayContent)?.bytes()?.decodeToString()
            )
        )
        return HttpResponseData(
            statusCode = response.statusCode,
            requestTime = GMTDate(),
            headers = response.headers,
            version = HttpProtocolVersion.HTTP_1_1,
            body = response.body?.let { ByteReadChannel(it) } ?: ByteReadChannel.Empty,
            callContext = callContext
        )
    }
}

internal class HttpClientMockEngineFactory(
    private val config: HttpClientMockConfig,
    private val backendServer: BackendServer,
) : HttpClientEngineFactory<HttpClientMockConfig> {
    override fun create(
        block: HttpClientMockConfig.() -> Unit
    ): HttpClientEngine =
        HttpClientMockEngine(
            config = config.apply(block),
            backendServer = backendServer
        )
}
