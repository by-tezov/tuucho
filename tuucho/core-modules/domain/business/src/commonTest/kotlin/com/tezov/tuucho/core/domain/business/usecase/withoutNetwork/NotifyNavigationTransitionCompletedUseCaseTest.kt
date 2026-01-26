package com.tezov.tuucho.core.domain.business.usecase.withoutNetwork

import com.tezov.tuucho.core.domain.business.protocol.repository.NavigationRepositoryProtocol
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifyNoMoreCalls
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class NotifyNavigationTransitionCompletedUseCaseTest {
    private lateinit var navigationAnimatorStackRepository: NavigationRepositoryProtocol.StackTransition

    private lateinit var sut: NotifyNavigationTransitionCompletedUseCase

    @BeforeTest
    fun setup() {
        navigationAnimatorStackRepository = mock()
        sut = NotifyNavigationTransitionCompletedUseCase(
            navigationAnimatorStackRepository = navigationAnimatorStackRepository
        )
    }

    @AfterTest
    fun tearDown() {
        verifyNoMoreCalls(navigationAnimatorStackRepository)
    }

    @Test
    fun `invoke triggers notifyTransitionCompleted`() = runTest {
        everySuspend { navigationAnimatorStackRepository.notifyTransitionCompleted() } returns Unit

        sut.invoke(Unit)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            navigationAnimatorStackRepository.notifyTransitionCompleted()
        }
    }
}
