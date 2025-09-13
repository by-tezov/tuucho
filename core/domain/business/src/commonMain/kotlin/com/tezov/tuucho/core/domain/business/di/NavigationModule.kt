package com.tezov.tuucho.core.domain.business.di

import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRouteIdGenerator
import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationStackRouteRepository
import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationStackScreenRepository
import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationStackTransitionRepository
import com.tezov.tuucho.core.domain.business.protocol.IdGeneratorProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.NavigationRepositoryProtocol
import org.koin.core.qualifier.named
import org.koin.dsl.module

object NavigationModule {

    object Name {
        val ID_GENERATOR = named("NavigationModule.Name.ID_GENERATOR")
    }

    internal operator fun invoke() = module {

        single<IdGeneratorProtocol>(Name.ID_GENERATOR) {
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


