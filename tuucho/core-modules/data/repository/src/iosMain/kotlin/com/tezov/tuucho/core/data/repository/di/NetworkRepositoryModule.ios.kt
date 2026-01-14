package com.tezov.tuucho.core.data.repository.di

import com.tezov.tuucho.core.data.repository.di.NetworkRepositoryModule.Name.HTTP_CLIENT_ENGINE
import com.tezov.tuucho.core.domain.business.di.KoinMass.Companion.module
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.darwin.Darwin

internal object NetworkRepositoryModuleIos {
    object FlavorDefault {
        fun invoke() = module(ModuleContextData.Main) {
            factory<HttpClientEngineFactory<*>>(HTTP_CLIENT_ENGINE) {
                getOrNull() ?: Darwin
            }
        }
    }

    fun invoke() = NetworkRepositoryModuleIosFlavor.invoke()
}
