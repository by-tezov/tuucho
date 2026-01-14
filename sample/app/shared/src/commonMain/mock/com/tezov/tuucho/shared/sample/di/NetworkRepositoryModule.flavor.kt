package com.tezov.tuucho.shared.sample.di

import com.tezov.tuucho.core.data.repository.di.ModuleContextData
import com.tezov.tuucho.core.data.repository.di.NetworkRepositoryModule
import com.tezov.tuucho.core.domain.business._system.koin.BindOrdered.bindOrdered
import com.tezov.tuucho.core.domain.business._system.koin.BindOrdered.getAllOrdered
import com.tezov.tuucho.core.domain.business.di.KoinMass.Companion.module
import com.tezov.tuucho.shared.sample.repository.network.HttpClientMockConfig
import com.tezov.tuucho.shared.sample.repository.network.HttpClientMockEngineFactory
import com.tezov.tuucho.shared.sample.repository.network.backendServer.BackendServer
import com.tezov.tuucho.shared.sample.repository.network.backendServer.guard.AuthGuard
import com.tezov.tuucho.shared.sample.repository.network.backendServer.guard.AuthGuardOptional
import com.tezov.tuucho.shared.sample.repository.network.backendServer.protocol.ServiceProtocol
import com.tezov.tuucho.shared.sample.repository.network.backendServer.service.HealthService
import com.tezov.tuucho.shared.sample.repository.network.backendServer.service.ResourceService
import com.tezov.tuucho.shared.sample.repository.network.backendServer.service.SendFormLoginService
import com.tezov.tuucho.shared.sample.repository.network.backendServer.service.SendService
import com.tezov.tuucho.shared.sample.repository.network.backendServer.store.LoginTokenStore
import io.ktor.client.engine.HttpClientEngineFactory
import org.koin.core.module.Module

internal object NetworkRepositoryModuleFlavor {
    fun invoke() = module(ModuleContextData.Main) {
        misc()
        guards()
        services()

        single<BackendServer> {
            BackendServer(
                serverUrl = get<NetworkRepositoryModule.Config>().baseUrl,
                services = getAllOrdered<ServiceProtocol>()
            )
        }

        factory<HttpClientMockConfig> {
            HttpClientMockConfig()
        }

        factory<HttpClientEngineFactory<*>> {
            HttpClientMockEngineFactory(
                config = get(),
                backendServer = get()
            )
        }
    }

    private fun Module.misc() {
        single {
            LoginTokenStore(
                useCaseExecutor = get(),
                saveKeyValueToStore = get(),
                getValueOrNullFromStore = get(),
            )
        }
    }

    private fun Module.guards() {
        factory {
            AuthGuard(
                loginTokenStore = get()
            )
        }

        factory {
            AuthGuardOptional(
                authGuard = get()
            )
        }
    }

    private fun Module.services() {

        factory {
            SendFormLoginService(
                loginTokenStore = get()
            )
        } bindOrdered ServiceProtocol::class

        factory {
            SendService(
                guards = listOf(get<AuthGuard>())
            )
        } bindOrdered ServiceProtocol::class

        factory {
            ResourceService(
                assets = get(),
                guards = listOf(get<AuthGuard>())
            )
        } bindOrdered ServiceProtocol::class

        factory {
            HealthService(
                guards = listOf(get<AuthGuardOptional>())
            )
        } bindOrdered ServiceProtocol::class

    }
}
