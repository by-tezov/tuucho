package com.tezov.tuucho.core.data.di

import com.tezov.tuucho.core.data.database.Database
import com.tezov.tuucho.core.data.network.response.JsonResponse
import com.tezov.tuucho.core.data.network.service.MaterialNetworkHttpRequest
import com.tezov.tuucho.core.data.network.service.MaterialNetworkService
import com.tezov.tuucho.core.data.parser.assembler.MaterialAssembler
import com.tezov.tuucho.core.data.parser.breaker.MaterialBreaker
import com.tezov.tuucho.core.data.parser.rectifier.MaterialRectifier
import com.tezov.tuucho.core.data.repository.MaterialCacheRepository
import com.tezov.tuucho.core.data.repository.MaterialRepositoryImpl
import com.tezov.tuucho.core.domain.protocol.MaterialRepositoryProtocol
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import org.koin.dsl.module
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

object MaterialRepositoryModule {

    internal operator fun invoke() = module {
        single<OkHttpClient> {
            OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .build()
        }

        single<Retrofit> {
            Retrofit.Builder()
                .baseUrl("http://10.0.2.2:3000/")
                .addCallAdapterFactory(JsonResponse.CallAdapterFactory())
                .client(get<OkHttpClient>())
                .build()
        }

        single<MaterialNetworkService> {
            MaterialNetworkService(
                materialNetworkHttpRequest = get<Retrofit>().create(MaterialNetworkHttpRequest::class.java),
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
            MaterialRepositoryImpl(
                materialNetworkService = get<MaterialNetworkService>(),
                materialCacheRepository = get<MaterialCacheRepository>(),
            )
        }
    }
}
