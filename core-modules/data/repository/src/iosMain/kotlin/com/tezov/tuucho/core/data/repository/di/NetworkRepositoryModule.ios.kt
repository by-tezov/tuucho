package com.tezov.tuucho.core.data.repository.di

import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.darwin.Darwin
import org.koin.core.module.Module

internal object NetworkRepositoryModuleIos {
    object FlavorDefault {
        fun invoke() = object : ModuleProtocol {
            override val group = ModuleGroupData.Main

            override fun Module.declaration() {
                factory<HttpClientEngineFactory<*>> {
                    Darwin
                }
            }
        }
    }

    fun invoke() = NetworkRepositoryModuleIosFlavor.invoke()
}
