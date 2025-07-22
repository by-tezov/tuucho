package com.tezov.tuucho.core.data.di

import com.tezov.tuucho.core.data.BuildConfig
import com.tezov.tuucho.core.data.database.Database
import com.tezov.tuucho.core.data.network.service.MaterialNetworkHttpRequest
import com.tezov.tuucho.core.data.network.service.MaterialNetworkService
import com.tezov.tuucho.core.data.parser.assembler.MaterialAssembler
import com.tezov.tuucho.core.data.parser.breaker.MaterialBreaker
import com.tezov.tuucho.core.data.parser.rectifier.MaterialRectifier
import com.tezov.tuucho.core.data.repository.MaterialCacheRepository
import com.tezov.tuucho.core.data.repository.MaterialRepository
import com.tezov.tuucho.core.domain.protocol.MaterialRepositoryProtocol
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module
import java.util.concurrent.TimeUnit

object MaterialRepositoryModule {

    internal operator fun invoke() = module {
        single<HttpClient> {
            HttpClient(OkHttp) {
                install(ContentNegotiation) {
                    json(get<Json>())
                }
                install(HttpTimeout) {
                    connectTimeoutMillis = TimeUnit.MILLISECONDS.convert(5, TimeUnit.SECONDS)
                    socketTimeoutMillis = TimeUnit.MILLISECONDS.convert(5, TimeUnit.SECONDS)
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
                baseUrl = "http://10.0.2.2:3000" //TODO
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
                database = get<Database>(),
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
