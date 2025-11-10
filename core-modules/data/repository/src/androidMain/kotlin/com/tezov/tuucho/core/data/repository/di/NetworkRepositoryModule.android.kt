package com.tezov.tuucho.core.data.repository.di

import com.tezov.tuucho.core.data.repository.di.NetworkRepositoryModule.Name.HTTP_CLIENT_ENGINE
import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol.Companion.module
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.okhttp.OkHttp

internal object NetworkRepositoryModuleAndroid {
    object FlavorDefault {
        fun invoke() = module(ModuleGroupData.Main) {
            factory<HttpClientEngineFactory<*>>(HTTP_CLIENT_ENGINE) {
                getOrNull() ?: OkHttp
            }
        }
    }

    fun invoke() = NetworkRepositoryModuleAndroidFlavor.invoke()
}
