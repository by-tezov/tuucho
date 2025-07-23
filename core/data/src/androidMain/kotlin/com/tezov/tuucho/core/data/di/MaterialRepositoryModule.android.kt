package com.tezov.tuucho.core.data.di

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.dsl.module

actual fun MaterialRepositoryModule.serverUrlEndpoint() = "http://10.0.2.2:3000"

object MaterialRepositoryModuleAndroid {

    internal operator fun invoke() = module {

        factory <HttpClientEngineFactory<*>> {
            OkHttp
        }
    }

}