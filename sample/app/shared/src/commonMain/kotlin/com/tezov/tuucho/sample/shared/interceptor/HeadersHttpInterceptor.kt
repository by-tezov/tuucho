package com.tezov.tuucho.sample.shared.interceptor

import com.tezov.tuucho.core.data.repository.network.HttpInterceptor
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol.Next.Companion.invoke
import com.tezov.tuucho.sample.shared.di.InterceptorModule
import io.ktor.client.request.HttpResponseData
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.flow.FlowCollector

class HeadersHttpInterceptor(
    private val config: InterceptorModule.Config
) : HttpInterceptor {
    override suspend fun ProducerScope<HttpResponseData>.process(
        context: HttpInterceptor.Context,
        next: MiddlewareProtocol.Next<HttpInterceptor.Context, HttpResponseData>?
    ) {
        with(context.builder) {
            headers.append("platform", config.headerPlatform)
            next.invoke(context)
        }
    }
}
