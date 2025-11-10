package com.tezov.tuucho.shared.sample.di

import com.tezov.tuucho.core.barrel.di.ModuleGroupCore
import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol.Companion.module
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.okhttp.OkHttp

internal object NetworkModuleAndroid {

    fun invoke() = module(ModuleGroupCore.Main) {
        factory<HttpClientEngineFactory<*>> {
            OkHttp
        }
    }
}
