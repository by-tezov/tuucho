package com.tezov.tuucho.shared.sample.interceptor

import com.tezov.tuucho.core.data.repository.network.HttpInterceptor
import com.tezov.tuucho.shared.sample.di.InterceptorModule
import io.ktor.client.request.HttpRequestBuilder

class HeadersHttpInterceptor(
    private val config: InterceptorModule.Config
) : HttpInterceptor.Node {

    override suspend fun intercept(
        builder: HttpRequestBuilder
    ) {
        builder.headers.append("platform", config.headerPlatform)
    }
}
