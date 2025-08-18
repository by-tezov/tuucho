package com.tezov.tuucho.core.domain.business.di

import com.tezov.tuucho.core.domain.business.navigation.NavigationRouteIdGenerator
import com.tezov.tuucho.core.domain.business.navigation.NavigationStackRouteRepository
import com.tezov.tuucho.core.domain.business.navigation.NavigationStackScreenRepository
import com.tezov.tuucho.core.domain.business.navigation.NavigationStackTransitionRepository
import com.tezov.tuucho.core.domain.business.protocol.repository.NavigationRepositoryProtocol
import kotlinx.serialization.json.Json
import org.koin.dsl.module

object MiscModule {

    internal operator fun invoke() = module {
        single<Json> {
            Json {
                ignoreUnknownKeys = true
                encodeDefaults = true
                explicitNulls = true
            }
        }

        single<NavigationRouteIdGenerator> {
            NavigationRouteIdGenerator()
        }

        single<NavigationRepositoryProtocol.StackRoute> {
            NavigationStackRouteRepository(
                coroutineScopes = get(),
            )
        }
        single<NavigationRepositoryProtocol.StackScreen> {
            NavigationStackScreenRepository(
                coroutineScopes = get(),
                screenRenderer = get()
            )
        }

        single<NavigationRepositoryProtocol.StackTransition> {
            NavigationStackTransitionRepository(
                coroutineScopes = get(),
                useCaseExecutor = get(),
                navigationStackTransitionHelperFactory = get(),
            )
        }


    }

}


