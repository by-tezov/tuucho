package com.tezov.tuucho.core.data.di

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.darwin.Darwin
import org.koin.dsl.module

actual fun MaterialRepositoryModule.serverUrlEndpoint() = "http://127.0.0.1:3000"

object MaterialRepositoryModuleIos {

    internal operator fun invoke() = module {

        factory<HttpClientEngineFactory<*>> {
            Darwin
        }

    }

}