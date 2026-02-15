package com.tezov.tuucho.core.domain.business.di

import com.tezov.tuucho.core.domain.business._system.koin.KoinMass.Companion.module
import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationMaterialCacheRepository
import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRouteIdGenerator
import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationStackRouteRepository
import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationStackScreenRepository
import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationStackTransitionRepository
import com.tezov.tuucho.core.domain.business.protocol.repository.NavigationRepositoryProtocol
import org.koin.dsl.bind
import org.koin.plugin.module.dsl.factory
import org.koin.plugin.module.dsl.single

internal object NavigationModule {
    fun invoke() = module(ModuleContextDomain.Main) {
        factory<NavigationRouteIdGenerator>()
        single<NavigationMaterialCacheRepository>() bind NavigationRepositoryProtocol.MaterialCache::class
        single<NavigationStackRouteRepository>() bind NavigationRepositoryProtocol.StackRoute::class
        single<NavigationStackScreenRepository>() bind NavigationRepositoryProtocol.StackScreen::class
        single<NavigationStackTransitionRepository>() bind NavigationRepositoryProtocol.StackTransition::class
    }
}
