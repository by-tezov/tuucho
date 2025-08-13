package com.tezov.tuucho.core.domain.business.usecase

import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.NavigationRepositoryProtocol
import com.tezov.tuucho.core.domain.business.protocol.screen.ScreenProtocol
import com.tezov.tuucho.core.domain.business.usecase.GetVisibleScreensUseCase.Output

class GetVisibleScreensUseCase(
    private val navigationStackScreenRepository: NavigationRepositoryProtocol.StackScreen,
    private val navigationStackAnimatorRepository: NavigationRepositoryProtocol.StackAnimator,
) : UseCaseProtocol.Async<Unit, Output> {

    data class Output(
        val screens: List<ScreenProtocol>,
    )

    override suspend fun invoke(input: Unit): Output {
        return Output(
            screens = navigationStackAnimatorRepository
                .getVisibleRoutes()
                .mapNotNull { navigationStackScreenRepository.getScreenOrNull(it) }
        )
    }
}