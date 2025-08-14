package com.tezov.tuucho.core.domain.business.usecase

import com.tezov.tuucho.core.domain.business.jsonSchema._system.onScope
import com.tezov.tuucho.core.domain.business.jsonSchema.material.setting.SettingSchema
import com.tezov.tuucho.core.domain.business.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.navigation.NavigationRouteIdGenerator
import com.tezov.tuucho.core.domain.business.protocol.CoroutineScopesProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.MaterialRepositoryProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.NavigationRepositoryProtocol
import com.tezov.tuucho.core.domain.business.usecase.NavigateToUrlUseCase.Input

class NavigateToUrlUseCase(
    private val coroutineScopes: CoroutineScopesProtocol,
    private val retrieveMaterialRepository: MaterialRepositoryProtocol.Retrieve,
    private val navigationRouteIdGenerator: NavigationRouteIdGenerator,
    private val navigationStackRouteRepository: NavigationRepositoryProtocol.StackRoute,
    private val navigationStackScreenRepository: NavigationRepositoryProtocol.StackScreen,
    private val navigationStackTransitionRepository: NavigationRepositoryProtocol.StackTransition,
    private val shadowerMaterialRepository: MaterialRepositoryProtocol.Shadower,
) : UseCaseProtocol.Sync<Input, Unit> {

    data class Input(
        val url: String,
    )

    override fun invoke(input: Input) {
        coroutineScopes.navigation.async {
            if(navigationStackTransitionRepository.isBusy()){
                //throw DomainException.Default("Navigation is not ready to accept new request")
                return@async
            }
            with(input) {
                val componentObject = retrieveMaterialRepository.process(url)
                val newRoute = navigationStackRouteRepository.push(
                    route = NavigationRoute.Url(navigationRouteIdGenerator.generate(), url),
                    navigationOptionObject = componentObject
                        .onScope(SettingSchema.Root::Scope).navigationOption
                )
                newRoute?.let {
                    navigationStackScreenRepository.push(
                        route = it,
                        componentObject = componentObject
                    )
                    shadowerMaterialRepository.process(url, componentObject)
                }
                navigationStackTransitionRepository.swallow(
                    routes = navigationStackRouteRepository.routes(),
                    animationOptionObject = componentObject
                        .onScope(SettingSchema.Root::Scope).animationOption,
                )
                navigationStackScreenRepository.intersect(
                    routes = navigationStackRouteRepository.routes()
                )
            }
        }
    }

}