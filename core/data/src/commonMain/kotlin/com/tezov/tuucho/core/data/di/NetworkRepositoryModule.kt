package com.tezov.tuucho.core.data.di

import com.tezov.tuucho.core.data.exception.DataException
import com.tezov.tuucho.core.data.network.MaterialNetworkSource
import com.tezov.tuucho.core.data.network.NetworkHttpRequest
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module

expect fun serverUrlEndpoint(): String //TODO external config file (android, ios, common)

private const val serverConnectTimeoutMillis = 5000L
private const val serverSocketTimeoutMillis = 5000L

object NetworkRepositoryModule {

    internal operator fun invoke() = module {
        factory<HttpClient> {
            HttpClient(get<HttpClientEngineFactory<*>>()) {
                install(ContentNegotiation) {
                    json(get<Json>())
                }
                install(HttpTimeout) {
                    connectTimeoutMillis = serverConnectTimeoutMillis
                    socketTimeoutMillis = serverSocketTimeoutMillis
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
            }
        }

        factory<NetworkHttpRequest> {
            NetworkHttpRequest(
                httpClient = get(),
                baseUrl = serverUrlEndpoint()
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
