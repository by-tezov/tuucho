package com.tezov.tuucho.core.data.repository.network

import com.tezov.tuucho.core.data.repository.network.HttpInterceptor.LocalResponse
import io.ktor.client.call.HttpClientCall
import io.ktor.client.plugins.api.ClientPlugin
import io.ktor.client.plugins.api.Send
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.HttpResponseData
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpProtocolVersion
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.util.AttributeKey
import io.ktor.util.date.GMTDate
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.InternalAPI

object HttpInterceptor {
    val LocalResponse = AttributeKey<ByteReadChannel>("HttpInterceptor.LocalResponse")

    fun interface Node {
        suspend fun intercept(
            builder: HttpRequestBuilder
        )
    }

    class Config {
        internal var nodes = emptyList<Node>()
    }
}

val HttpInterceptorPlugin: ClientPlugin<HttpInterceptor.Config> = createClientPlugin(
    "HttpInterceptor",
    HttpInterceptor::Config
) {
    val nodes = pluginConfig.nodes
    on(Send) { builder ->
        if (nodes.isNotEmpty()) {
            nodes.forEach {
                it.intercept(builder)
            }
            builder.attributes.getOrNull(LocalResponse)?.let {
                val localResponse = HttpResponseData(
                    statusCode = HttpStatusCode.OK,
                    requestTime = GMTDate(),
                    headers = headersOf(HttpHeaders.ContentType, "application/json"),
                    version = HttpProtocolVersion.HTTP_1_1,
                    body = it,
                    callContext = builder.executionContext
                )
                @OptIn(InternalAPI::class)
                return@on HttpClientCall(client, builder.build(), localResponse)
            }
        }
        proceed(builder)
    }
}
