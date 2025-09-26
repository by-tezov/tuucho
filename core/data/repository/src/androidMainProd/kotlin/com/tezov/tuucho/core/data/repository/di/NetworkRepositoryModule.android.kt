package com.tezov.tuucho.core.data.repository.di

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.core.module.Module
import org.koin.dsl.module

object NetworkRepositoryModuleAndroidFlavor {

    operator fun invoke() = module {

        factory <HttpClientEngineFactory<*>> {
            OkHttp
        }
    }

}