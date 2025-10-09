package com.tezov.tuucho.core.data.repository.di

import com.tezov.tuucho.core.data.repository.exception.DataException
import com.tezov.tuucho.core.data.repository.network.MaterialNetworkSource
import com.tezov.tuucho.core.data.repository.network.NetworkHttpRequest
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.plugin
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module

object NetworkRepositoryModule {

    interface RequestInterceptor {
        suspend fun intercept(builder: HttpRequestBuilder)
    }

    interface Config {
        val timeoutMillis: Long
        val version: String
        val baseUrl: String
        val resourceEndpoint: String
        val sendEndpoint: String
    }

    internal fun invoke() = module {
        factory<HttpClient> {
            HttpClient(get<HttpClientEngineFactory<*>>()) {
                install(ContentNegotiation) {
                    json(get<Json>())
                }
                install(HttpTimeout) {
                    with(get<Config>()) {
                        connectTimeoutMillis = timeoutMillis
                        socketTimeoutMillis = timeoutMillis
                    }
                }

                HttpResponseValidator {
                    validateResponse { response ->
                        val statusCode = response.status.value
                        if (statusCode !in 200..299) {
                            throw DataException.Default("Bad response received: $response")
                        }
                    }
                    handleResponseExceptionWithRequest { cause, _ -> throw cause }
                }
            }.apply {
                val interceptors = getAll<RequestInterceptor>()
                if(interceptors.isNotEmpty()) {
                    plugin(HttpSend).intercept { requestBuilder ->
                        interceptors.forEach { it.intercept(requestBuilder) }
                        execute(requestBuilder)
                    }
                }
            }
        }

        factory<NetworkHttpRequest> {
            NetworkHttpRequest(
                httpClient = get(),
                config = get<Config>()
            )
        }

        single<MaterialNetworkSource> {
            MaterialNetworkSource(
                networkHttpRequest = get(),
                jsonConverter = get()
            )
        }
    }
}
