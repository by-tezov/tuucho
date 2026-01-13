package com.tezov.tuucho.core.domain.business.di

import com.tezov.tuucho.core.domain.business.di.Koin.Companion.module
import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRouteIdGenerator
import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationStackRouteRepository
import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationStackScreenRepository
import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationStackTransitionRepository
import com.tezov.tuucho.core.domain.business.protocol.repository.NavigationRepositoryProtocol
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind

internal object NavigationModule {
    fun invoke() = module(ModuleGroupDomain.Main) {
        factoryOf(::NavigationRouteIdGenerator)
        singleOf(::NavigationStackRouteRepository) bind NavigationRepositoryProtocol.StackRoute::class
        singleOf(::NavigationStackScreenRepository) bind NavigationRepositoryProtocol.StackScreen::class
        singleOf(::NavigationStackTransitionRepository) bind NavigationRepositoryProtocol.StackTransition::class
    }
}
