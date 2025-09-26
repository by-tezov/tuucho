package com.tezov.tuucho.core.data.repository.di

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.dsl.module

actual fun serverUrlEndpoint() = "http://10.0.2.2:3000"

internal object NetworkRepositoryModuleAndroid {

    object FlavorDefault {

        fun invoke() = module {

            factory <HttpClientEngineFactory<*>> {
                OkHttp
            }
        }

    }

    fun invoke() = NetworkRepositoryModuleAndroidFlavor.invoke()

}