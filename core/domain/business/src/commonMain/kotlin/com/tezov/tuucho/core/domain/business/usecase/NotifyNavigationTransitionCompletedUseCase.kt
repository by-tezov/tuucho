package com.tezov.tuucho.core.domain.business.usecase

import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.NavigationRepositoryProtocol

class NotifyNavigationTransitionCompletedUseCase(
    private val navigationAnimatorStackRepository: NavigationRepositoryProtocol.StackTransition,
) : UseCaseProtocol.Async<Unit, Unit> {

    override suspend fun invoke(input: Unit) {
        navigationAnimatorStackRepository.notifyTransitionCompleted()
    }

}