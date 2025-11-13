package com.tezov.tuucho.shared.sample.interceptor

import com.tezov.tuucho.core.data.repository.network.HttpInterceptor
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol
import com.tezov.tuucho.shared.sample.di.InterceptorModule
import io.ktor.client.request.HttpResponseData

class HeadersHttpInterceptor(
    private val config: InterceptorModule.Config
) : HttpInterceptor {
    override suspend fun process(
        context: HttpInterceptor.Context,
        next: MiddlewareProtocol.Next<HttpInterceptor.Context, HttpResponseData>
    ): HttpResponseData {
        context.builder.headers.append("platform", config.headerPlatform)
        return next.invoke(context)
    }
}
