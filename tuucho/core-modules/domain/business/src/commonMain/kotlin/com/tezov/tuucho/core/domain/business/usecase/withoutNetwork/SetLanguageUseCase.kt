package com.tezov.tuucho.core.domain.business.usecase.withoutNetwork

import com.tezov.tuucho.core.domain.business.model.LanguageModelDomain
import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.NavigationRepositoryProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.SystemPlatformRepositoryProtocol
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.SetLanguageUseCase.Input
import com.tezov.tuucho.core.domain.test._system.OpenForTest

@OpenForTest
class SetLanguageUseCase(
    private val platformRepository: SystemPlatformRepositoryProtocol,
    private val navigationStackRouteRepository: NavigationRepositoryProtocol.StackRoute,
    private val navigationStackScreenRepository: NavigationRepositoryProtocol.StackScreen,
) : UseCaseProtocol.Async<Input, Unit> {
    data class Input(
        val language: LanguageModelDomain,
    )

    override suspend fun invoke(
        input: Input
    ) {
        platformRepository.setCurrentLanguage(input.language)
        val routes = navigationStackRouteRepository.routes()
        val screens = navigationStackScreenRepository.getScreens()
        val orderedScreens = screens.sortedBy { screen ->
            routes.indexOf(screen.route)
        }
        orderedScreens.asReversed().forEach { it.recreateViews() }
    }
}
