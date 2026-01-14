package com.tezov.tuucho.sample.shared.di

import com.tezov.tuucho.core.barrel.di.ModuleContextCore
import com.tezov.tuucho.core.domain.business.di.KoinMass.Companion.module
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.okhttp.OkHttp

internal object NetworkModuleAndroid {

    object FlavorDefault {
        fun invoke() = module(ModuleContextCore.Main) {
            factory<HttpClientEngineFactory<*>> {
                OkHttp
            }
        }
    }

    fun invoke() = NetworkRepositoryModuleAndroidFlavor.invoke()
}
