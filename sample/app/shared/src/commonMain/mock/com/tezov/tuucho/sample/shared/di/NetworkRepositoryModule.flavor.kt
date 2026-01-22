package com.tezov.tuucho.sample.shared.di

import com.tezov.tuucho.core.data.repository.di.ModuleContextData
import com.tezov.tuucho.core.data.repository.di.NetworkModule
import com.tezov.tuucho.core.domain.business._system.koin.BindOrdered.bindOrdered
import com.tezov.tuucho.core.domain.business._system.koin.BindOrdered.getAllOrdered
import com.tezov.tuucho.core.domain.business._system.koin.KoinMass.Companion.module
import com.tezov.tuucho.sample.shared.repository.network.HttpClientMockConfig
import com.tezov.tuucho.sample.shared.repository.network.HttpClientMockEngineFactory
import com.tezov.tuucho.sample.shared.repository.network.backendServer.BackendServer
import com.tezov.tuucho.sample.shared.repository.network.backendServer.guard.AuthGuard
import com.tezov.tuucho.sample.shared.repository.network.backendServer.guard.AuthGuardOptional
import com.tezov.tuucho.sample.shared.repository.network.backendServer.protocol.ServiceProtocol
import com.tezov.tuucho.sample.shared.repository.network.backendServer.service.HealthService
import com.tezov.tuucho.sample.shared.repository.network.backendServer.service.ImageService
import com.tezov.tuucho.sample.shared.repository.network.backendServer.service.ResourceService
import com.tezov.tuucho.sample.shared.repository.network.backendServer.service.SendFormLoginService
import com.tezov.tuucho.sample.shared.repository.network.backendServer.service.SendService
import com.tezov.tuucho.sample.shared.repository.network.backendServer.store.LoginTokenStore
import io.ktor.client.engine.HttpClientEngineFactory
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind

internal object NetworkRepositoryModuleFlavor {
    fun invoke() = module(ModuleContextData.Main) {
        misc()
        guards()
        services()

        single {
            BackendServer(
                serverUrl = get<NetworkModule.Config>().baseUrl,
                services = getAllOrdered<ServiceProtocol>()
            )
        }

        factoryOf(::HttpClientMockConfig)
        factoryOf(::HttpClientMockEngineFactory) bind HttpClientEngineFactory::class
    }

    private fun Module.misc() {
        singleOf(::LoginTokenStore)
    }

    private fun Module.guards() {
        factoryOf(::AuthGuard)
        factoryOf(::AuthGuardOptional)
    }

    private fun Module.services() {
        factory {
            HealthService(
                config = get(),
                guards = listOf(get<AuthGuardOptional>())
            )
        } bindOrdered ServiceProtocol::class

        factory {
            ImageService(
                config = get(),
                assets = get(),
                guards = listOf(get<AuthGuard>())
            )
        } bindOrdered ServiceProtocol::class

        factory {
            ResourceService(
                config = get(),
                assets = get(),
                guards = listOf(get<AuthGuard>())
            )
        } bindOrdered ServiceProtocol::class

        factoryOf(::SendFormLoginService) bindOrdered ServiceProtocol::class

        factory {
            SendService(
                config = get(),
                guards = listOf(get<AuthGuard>())
            )
        } bindOrdered ServiceProtocol::class
    }
}
