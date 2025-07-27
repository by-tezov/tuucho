package com.tezov.tuucho.core.data.di

import com.tezov.tuucho.core.data.database.MaterialCacheSource
import com.tezov.tuucho.core.data.exception.DataException
import com.tezov.tuucho.core.data.network.MaterialNetworkHttpRequest
import com.tezov.tuucho.core.data.network.MaterialNetworkSource
import com.tezov.tuucho.core.data.parser.assembler.MaterialAssembler
import com.tezov.tuucho.core.data.parser.breaker.MaterialBreaker
import com.tezov.tuucho.core.data.parser.rectifier.MaterialRectifier
import com.tezov.tuucho.core.data.repository.RefreshCacheMaterialRepository
import com.tezov.tuucho.core.data.repository.RetrieveMaterialRepository
import com.tezov.tuucho.core.data.repository.SendDataMaterialRepository
import com.tezov.tuucho.core.domain.protocol.RefreshCacheMaterialRepositoryProtocol
import com.tezov.tuucho.core.domain.protocol.RetrieveMaterialRepositoryProtocol
import com.tezov.tuucho.core.domain.protocol.SendDataMaterialRepositoryProtocol
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpTimeout
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
                            throw DataException.Default("Bad response received: $response")
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

        single<MaterialNetworkSource> {
            MaterialNetworkSource(
                materialNetworkHttpRequest = get<MaterialNetworkHttpRequest>(),
                materialRectifier = get<MaterialRectifier>(),
                jsonConverter = get<Json>()
            )
        }

        single<MaterialCacheSource> {
            MaterialCacheSource(
                jsonObjectQueries = get(),
                versioningQueries = get(),
                materialBreaker = get<MaterialBreaker>(),
                materialAssembler = get<MaterialAssembler>()
            )
        }

        single<SendDataMaterialRepositoryProtocol> {
            SendDataMaterialRepository(
                materialNetworkSource = get<MaterialNetworkSource>()
            )
        }

        single<RetrieveMaterialRepositoryProtocol> {
            RetrieveMaterialRepository(
                materialNetworkSource = get<MaterialNetworkSource>(),
                materialCacheSource = get<MaterialCacheSource>()
            )
        }

        single<RefreshCacheMaterialRepositoryProtocol> {
            RefreshCacheMaterialRepository(
                materialNetworkSource = get<MaterialNetworkSource>(),
                materialCacheSource = get<MaterialCacheSource>()
            )
        }


    }
}
