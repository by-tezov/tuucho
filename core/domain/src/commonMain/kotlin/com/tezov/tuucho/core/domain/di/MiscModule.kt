package com.tezov.tuucho.core.domain.di

import com.tezov.tuucho.core.domain.protocol.CoroutineDispatchersImpl
import com.tezov.tuucho.core.domain.protocol.CoroutineDispatchersProtocol
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import org.koin.dsl.module

object MiscModule {

    @OptIn(ExperimentalSerializationApi::class)
    internal operator fun invoke() = module {
        single<Json> {
            Json {
                ignoreUnknownKeys = true
                encodeDefaults = true
                explicitNulls = true
            }
        }

        single<CoroutineDispatchersProtocol> { CoroutineDispatchersImpl() }
    }

}


