package com.tezov.tuucho.sample.shared.interceptor

import com.tezov.tuucho.core.data.repository.assets.AssetsProtocol
import com.tezov.tuucho.core.data.repository.di.NetworkModule
import com.tezov.tuucho.core.data.repository.network.HttpInterceptor
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol
import com.tezov.tuucho.sample.shared._system.Page
import io.ktor.client.request.HttpResponseData
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpProtocolVersion
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.util.date.GMTDate
import io.ktor.utils.io.ByteReadChannel
import okio.buffer
import okio.use

class FailSafePageHttpInterceptor(
    private val config: NetworkModule.Config,
    private val assets: AssetsProtocol,
) : HttpInterceptor {
    override suspend fun process(
        context: HttpInterceptor.Context,
        next: MiddlewareProtocol.Next<HttpInterceptor.Context, HttpResponseData>?
    ) = with(context.builder) {
        val route = url.toString()
            .removePrefix("${config.baseUrl}/")
            .removePrefix("${config.version}/")
            .removePrefix(config.resourceEndpoint)

        if (route != "/${Page.failSafe}") return@with next?.invoke(context)

        HttpResponseData(
            statusCode = HttpStatusCode.OK,
            requestTime = GMTDate(),
            headers = headersOf(HttpHeaders.ContentType, "application/json"),
            version = HttpProtocolVersion.HTTP_1_1,
            body = ByteReadChannel(failSafeResponse),
            callContext = executionContext
        )
    }

    private val failSafeResponse
        get():String {
            val response = assets.readFile(
                AssetsProtocol.Request(path = "json/fail-safe-page-http-interceptor.json")
            )
            if (response is AssetsProtocol.Response.Failure) {
                throw response.error
            }
            val source = (response as AssetsProtocol.Response.Success).source
            return source.buffer().use { it.readUtf8() }
        }
}
