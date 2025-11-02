package com.tezov.tuucho.shared.sample.interceptor

import com.tezov.tuucho.core.data.repository.di.NetworkRepositoryModule
import com.tezov.tuucho.shared.sample.di.RequestInterceptorModule
import io.ktor.client.request.HttpRequestBuilder

class HeadersInterceptor(
    private val config: RequestInterceptorModule.Config,
) : NetworkRepositoryModule.RequestInterceptor {

    override suspend fun intercept(builder: HttpRequestBuilder) {
        builder.headers.append("platform", config.headerPlatform)
    }
}