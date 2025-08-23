package com.tezov.tuucho.core.data.di

import com.tezov.tuucho.core.data.network.HttpClientMockConfig
import com.tezov.tuucho.core.data.network.HttpClientMockEngineFactory
import com.tezov.tuucho.core.data.network.backendServer.BackendServer
import io.ktor.client.engine.HttpClientEngineFactory
import org.koin.core.module.Module

object NetworkRepositoryModuleFlavor {

    operator fun invoke(module: Module) = module.apply {

        single<BackendServer> {
            BackendServer()
        }

        factory<HttpClientMockConfig> {
            HttpClientMockConfig()
        }

        factory<HttpClientEngineFactory<*>> {
            HttpClientMockEngineFactory(
                config = get(),
                backendServer = get()
            )
        }
    }

}