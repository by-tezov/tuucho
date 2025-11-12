package com.tezov.tuucho.shared.sample.interceptor

import com.tezov.tuucho.core.data.repository.network.HttpInterceptor
import com.tezov.tuucho.shared.sample._system.Logger
import com.tezov.tuucho.shared.sample.di.RequestInterceptorModule
import io.ktor.client.request.HttpRequestBuilder

class LoggerInterceptor(
    private val logger: Logger
) : HttpInterceptor.Node {

    override suspend fun intercept(
        builder: HttpRequestBuilder
    ) {
        with(logger) {
            println("${builder.method} - ${builder.url}")
            builder.headers.names().takeIf { it.isNotEmpty() }?.let { names->
                println("-- Headers --")
                names.forEach {
                    println(it)
                }
                println("-------------")
            }
        }
    }
}
