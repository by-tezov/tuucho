package com.tezov.tuucho.core.data.repository.di

import com.tezov.tuucho.core.data.repository.exception.DataException
import com.tezov.tuucho.core.data.repository.network.HttpInterceptor
import com.tezov.tuucho.core.data.repository.network.HttpInterceptorPlugin
import com.tezov.tuucho.core.data.repository.network.NetworkHealthCheck
import com.tezov.tuucho.core.data.repository.network.NetworkJsonObject
import com.tezov.tuucho.core.data.repository.network.source.NetworkHttpRequestSource
import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol
import com.tezov.tuucho.core.domain.business.protocol.ServerHealthCheckProtocol
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.plugins.HttpCallValidator
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.Module

object NetworkRepositoryModule {
    interface Config {
        val timeoutMillis: Long
        val version: String
        val baseUrl: String
        val healthEndpoint: String
        val resourceEndpoint: String
        val sendEndpoint: String
    }

    internal fun invoke() = object : ModuleProtocol {
        override val group = ModuleGroupData.Main

        override fun Module.declaration() {
            single<HttpClient> {
                HttpClient(get<HttpClientEngineFactory<*>>()) {
                    install(ContentNegotiation) {
                        json(get<Json>())
                    }
                    install(HttpTimeout) {
                        with(get<Config>()) {
                            requestTimeoutMillis = timeoutMillis
                            connectTimeoutMillis = timeoutMillis
                            socketTimeoutMillis = timeoutMillis
                        }
                    }
                    install(HttpCallValidator) {
                        validateResponse { response ->
                            val statusCode = response.status.value
                            if (statusCode !in 200..299) {
                                throw DataException.Default("Bad response received: $response")
                            }
                        }
                        handleResponseExceptionWithRequest { cause, _ -> throw cause }
                    }
                    install(HttpInterceptorPlugin) {
                        nodes = getAll<HttpInterceptor.Node>()
                    }
                }
            }

            factory<NetworkHttpRequestSource> {
                NetworkHttpRequestSource(
                    httpClient = get(),
                    config = get()
                )
            }

            single<NetworkJsonObject> {
                NetworkJsonObject(
                    networkHttpRequestSource = get(),
                    jsonConverter = get()
                )
            }

            factory<ServerHealthCheckProtocol> {
                NetworkHealthCheck(
                    coroutineScopes = get(),
                    networkHttpRequestSource = get(),
                    jsonConverter = get()
                )
            }
        }
    }
}
