package com.tezov.tuucho.shared.sample.di

import com.tezov.tuucho.core.barrel.di.ModuleGroupCore
import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol.Companion.module
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.darwin.Darwin
import com.tezov.tuucho.shared.sample.repository.di.NetworkRepositoryModuleIosFlavor

internal object NetworkModuleIos {

    object FlavorDefault {
        fun invoke() = module(ModuleGroupCore.Main) {
            factory<HttpClientEngineFactory<*>> {
                Darwin
            }
        }
    }

    fun invoke() = NetworkRepositoryModuleIosFlavor.invoke()
}
