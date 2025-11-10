package com.tezov.tuucho.core.data.repository.di

import com.tezov.tuucho.core.data.repository.di.NetworkRepositoryModule.Name.HTTP_CLIENT_ENGINE
import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol
import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol.Companion.module
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.darwin.Darwin
import org.koin.core.module.Module

internal object NetworkRepositoryModuleIos {
    object FlavorDefault {
        fun invoke() = module(ModuleGroupData.Main) {
            factory<HttpClientEngineFactory<*>>(HTTP_CLIENT_ENGINE) {
                getOrNull() ?: Darwin
            }
        }
    }

    fun invoke() = NetworkRepositoryModuleIosFlavor.invoke()
}
