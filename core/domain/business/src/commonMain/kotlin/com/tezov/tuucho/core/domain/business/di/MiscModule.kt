package com.tezov.tuucho.core.domain.business.di

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


    }

}


