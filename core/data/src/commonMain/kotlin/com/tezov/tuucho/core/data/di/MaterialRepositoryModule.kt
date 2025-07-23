package com.tezov.tuucho.core.data.di

import com.tezov.tuucho.core.data.network.service.MaterialNetworkHttpRequest
import com.tezov.tuucho.core.data.network.service.MaterialNetworkService
import com.tezov.tuucho.core.data.parser.assembler.MaterialAssembler
import com.tezov.tuucho.core.data.parser.breaker.MaterialBreaker
import com.tezov.tuucho.core.data.parser.rectifier.MaterialRectifier
import com.tezov.tuucho.core.data.repository.MaterialCacheRepository
import com.tezov.tuucho.core.data.repository.MaterialRepository
import com.tezov.tuucho.core.domain.protocol.MaterialRepositoryProtocol
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module

expect fun MaterialRepositoryModule.serverUrlEndpoint():String //TODO external config file (android, ios, common)
private const val serverConnectTimeoutMillis = 5000L
private const val serverSocketTimeoutMillis = 5000L

object MaterialRepositoryModule {

    internal operator fun invoke() = module {

        single<HttpClient> {
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
                            throw ResponseException(response, "HTTP ${response.status} received")
                        }
                    }
                    handleResponseExceptionWithRequest { cause, _ -> throw cause }
                }
            }
        }

        single<MaterialNetworkHttpRequest> {
            MaterialNetworkHttpRequest(
                client = get<HttpClient>(),
                baseUrl = serverUrlEndpoint()
            )
        }

        single<MaterialNetworkService> {
            MaterialNetworkService(
                materialNetworkHttpRequest = get<MaterialNetworkHttpRequest>(),
                materialRectifier = get<MaterialRectifier>(),
                jsonConverter = get<Json>()
            )
        }

        single<MaterialCacheRepository> {
            MaterialCacheRepository(
                jsonObjectQueries = get(),
                versioningQueries = get(),
                materialBreaker = get<MaterialBreaker>(),
                materialAssembler = get<MaterialAssembler>()
            )
        }

        single<MaterialRepositoryProtocol> {
            MaterialRepository(
                materialNetworkService = get<MaterialNetworkService>(),
                materialCacheRepository = get<MaterialCacheRepository>(),
            )
        }


    }
}
