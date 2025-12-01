package com.tezov.tuucho.core.data.repository.di

import com.tezov.tuucho.core.data.repository.di.NetworkRepositoryModule.Name.HTTP_CLIENT_ENGINE
import com.tezov.tuucho.core.data.repository.exception.DataException
import com.tezov.tuucho.core.data.repository.network.HttpClientEngineFactory
import com.tezov.tuucho.core.data.repository.network.NetworkHealthCheck
import com.tezov.tuucho.core.data.repository.network.NetworkJsonObject
import com.tezov.tuucho.core.data.repository.network.source.NetworkHttpRequestSource
import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol.Companion.module
import com.tezov.tuucho.core.domain.business.protocol.ServerHealthCheckProtocol
import com.tezov.tuucho.core.domain.tool.extension.ExtensionKoin.getAllOrdered
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpCallValidator
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.named

object NetworkRepositoryModule {
    interface Config {
        val timeoutMillis: Long
        val version: String
        val baseUrl: String
        val healthEndpoint: String
        val resourceEndpoint: String
        val sendEndpoint: String
    }

    internal object Name {
        val HTTP_CLIENT_ENGINE = named("NetworkRepositoryModule.Name.HTTP_CLIENT_ENGINE")
    }

    internal fun invoke() = module(ModuleGroupData.Main) {
        single<HttpClient> {
            HttpClient(
                engineFactory = HttpClientEngineFactory(
                    engineFactory = get<io.ktor.client.engine.HttpClientEngineFactory<*>>(HTTP_CLIENT_ENGINE),
                    middlewareExecutor = get(),
                    interceptors = getAllOrdered()
                )
            ) {
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
                        @Suppress("MagicNumber")
                        if (statusCode !in 200..299) {
                            throw DataException.Default("Bad response received: $response")
                        }
                    }
                    handleResponseExceptionWithRequest { cause, _ ->
                        throw cause
                    }
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
