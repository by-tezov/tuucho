package com.tezov.tuucho.core.domain.di

import kotlinx.serialization.json.Json
import org.koin.dsl.module

object MaterialModule {

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
