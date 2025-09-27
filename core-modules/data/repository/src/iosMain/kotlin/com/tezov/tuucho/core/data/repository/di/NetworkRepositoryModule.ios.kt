package com.tezov.tuucho.core.data.repository.di

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.darwin.Darwin
import org.koin.dsl.module

internal object NetworkRepositoryModuleIos {

    object FlavorDefault {

        fun invoke() = module {

            factory<HttpClientEngineFactory<*>> {
                Darwin
            }
        }

    }

    fun invoke() = NetworkRepositoryModuleIosFlavor.invoke()

}