package com.tezov.tuucho.core.data.repository.di

import com.tezov.tuucho.core.data.repository.di.NetworkModule.Name.HTTP_CLIENT_ENGINE
import com.tezov.tuucho.core.domain.business._system.koin.KoinMass.Companion.module
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.cio.CIO

internal object NetworkModuleJvm {
    fun invoke() = module(ModuleContextData.Main) {
        single<HttpClientEngineFactory<*>>(HTTP_CLIENT_ENGINE) {
            getOrNull<HttpClientEngineFactory<*>>() ?: CIO
        }
    }
}
