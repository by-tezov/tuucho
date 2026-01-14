package com.tezov.tuucho.shared.sample.di

import com.tezov.tuucho.core.barrel.di.ModuleContextCore
import com.tezov.tuucho.core.domain.business.di.KoinMass.Companion.module
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.darwin.Darwin

internal object NetworkModuleIos {

    object FlavorDefault {
        fun invoke() = module(ModuleContextCore.Main) {
            factory<HttpClientEngineFactory<*>> {
                Darwin
            }
        }
    }

    fun invoke() = NetworkRepositoryModuleIosFlavor.invoke()
}
