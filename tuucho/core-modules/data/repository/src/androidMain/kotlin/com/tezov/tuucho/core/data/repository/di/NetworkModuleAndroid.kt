package com.tezov.tuucho.core.data.repository.di

import com.tezov.tuucho.core.data.repository.di.NetworkModule.Name.HTTP_CLIENT_ENGINE
import com.tezov.tuucho.core.domain.business._system.koin.KoinMass.Companion.module
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.okhttp.OkHttp

internal object NetworkModuleAndroid {
    fun invoke() = module(ModuleContextData.Main) {
        factory<HttpClientEngineFactory<*>>(HTTP_CLIENT_ENGINE) {
            getOrNull<HttpClientEngineFactory<*>>() ?: OkHttp
        }
    }
}
