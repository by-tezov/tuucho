package com.tezov.tuucho.core.domain.business.usecase.withoutNetwork

import com.tezov.tuucho.core.domain.business.protocol.UseCaseProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.NavigationRepositoryProtocol
import com.tezov.tuucho.core.domain.test._system.OpenForTest

@OpenForTest
class NotifyNavigationTransitionCompletedUseCase(
    private val navigationAnimatorStackRepository: NavigationRepositoryProtocol.StackTransition,
) : UseCaseProtocol.Async<Unit, Unit> {
    override suspend fun invoke(
        input: Unit
    ) {
        navigationAnimatorStackRepository.notifyTransitionCompleted()
    }
}
