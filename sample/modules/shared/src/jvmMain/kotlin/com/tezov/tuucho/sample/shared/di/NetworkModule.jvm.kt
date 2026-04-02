package com.tezov.tuucho.sample.shared.di

import com.tezov.tuucho.core.barrel.di.ModuleContextCore
import com.tezov.tuucho.core.domain.business._system.koin.KoinMass.Companion.module
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.cio.CIO

internal object NetworkModuleJvm {

    object FlavorDefault {
        fun invoke() = module(ModuleContextCore.Main) {
            factory<HttpClientEngineFactory<*>> {
                CIO
            }
        }
    }

    fun invoke() = NetworkRepositoryModuleJvmFlavor.invoke()
}
