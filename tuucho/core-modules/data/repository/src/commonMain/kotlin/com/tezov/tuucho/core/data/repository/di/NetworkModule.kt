package com.tezov.tuucho.core.data.repository.di

import com.tezov.tuucho.core.data.repository.di.NetworkModule.Name.HTTP_CLIENT_ENGINE
import com.tezov.tuucho.core.data.repository.exception.DataException
import com.tezov.tuucho.core.data.repository.network.HttpClientEngineFactory
import com.tezov.tuucho.core.data.repository.network.HttpNetworkSource
import com.tezov.tuucho.core.domain.business._system.koin.BindOrdered.getAllOrdered
import com.tezov.tuucho.core.domain.business._system.koin.KoinMass.Companion.module
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpCallValidator
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.factoryOf
import org.koin.core.qualifier.named

object NetworkModule {
    interface Config {
        val timeoutMillis: Long
        val version: String
        val baseUrl: String
        val healthEndpoint: String
        val resourceEndpoint: String
        val sendEndpoint: String
        val imageEndpoint: String
    }

    internal object Name {
        val HTTP_CLIENT_ENGINE get() = named("NetworkRepositoryModule.Name.HTTP_CLIENT_ENGINE")
    }

    internal fun invoke() = module(ModuleContextData.Main) {
        single {
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

        factoryOf(::HttpNetworkSource)
    }
}
