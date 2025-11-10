package com.tezov.tuucho.core.data.repository.di

import com.tezov.tuucho.core.data.repository.di.NetworkRepositoryModule.Name.HTTP_CLIENT_ENGINE
import com.tezov.tuucho.core.data.repository.network.HttpClientMockConfig
import com.tezov.tuucho.core.data.repository.network.HttpClientMockEngineFactory
import com.tezov.tuucho.core.data.repository.network.backendServer.BackendServer
import com.tezov.tuucho.core.data.repository.network.backendServer.protocol.ServiceProtocol
import com.tezov.tuucho.core.data.repository.network.backendServer.service.ResourceService
import com.tezov.tuucho.core.data.repository.network.backendServer.service.SendService
import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol.Companion.module
import io.ktor.client.engine.HttpClientEngineFactory
import org.koin.core.module.Module
import org.koin.dsl.bind

internal object NetworkRepositoryModuleFlavor {
    fun invoke() = module(ModuleGroupData.Main) {
        services()

        single<BackendServer> {
            BackendServer(
                serverUrl = get<NetworkRepositoryModule.Config>().baseUrl,
                services = getAll<ServiceProtocol>()
            )
        }

        factory<HttpClientMockConfig> {
            HttpClientMockConfig()
        }

        factory<HttpClientEngineFactory<*>>(HTTP_CLIENT_ENGINE) {
            HttpClientMockEngineFactory(
                config = get(),
                backendServer = get()
            )
        }
    }

    private fun Module.services() {
        factory<SendService> {
            SendService()
        } bind ServiceProtocol::class

        factory<ResourceService> {
            ResourceService(
                assets = get()
            )
        } bind ServiceProtocol::class
    }
}
