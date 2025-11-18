package com.tezov.tuucho.core.domain.business.di

import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRouteIdGenerator
import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationStackRouteRepository
import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationStackScreenRepository
import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationStackTransitionRepository
import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol.Companion.module
import com.tezov.tuucho.core.domain.business.protocol.repository.NavigationRepositoryProtocol

internal object NavigationModule {
    fun invoke() = module(ModuleGroupDomain.Main) {
        single<NavigationRouteIdGenerator> {
            NavigationRouteIdGenerator(
                idGenerator = get()
            )
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
