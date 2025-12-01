package com.tezov.tuucho.core.domain.business.usecase.withoutNetwork

import com.tezov.tuucho.core.domain.business.mock.CoroutineTestScope
import com.tezov.tuucho.core.domain.business.protocol.repository.NavigationRepositoryProtocol
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifyNoMoreCalls
import dev.mokkery.verifySuspend
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class NotifyNavigationTransitionCompletedUseCaseTest {
    private val coroutineTestScope = CoroutineTestScope()

    private lateinit var navigationAnimatorStackRepository: NavigationRepositoryProtocol.StackTransition

    private lateinit var sut: NotifyNavigationTransitionCompletedUseCase

    @BeforeTest
    fun setup() {
        coroutineTestScope.setup()
        navigationAnimatorStackRepository = mock()
        sut = NotifyNavigationTransitionCompletedUseCase(
            navigationAnimatorStackRepository = navigationAnimatorStackRepository
        )
    }

    @AfterTest
    fun tearDown() {
        coroutineTestScope.verifyNoMoreCalls()
        verifyNoMoreCalls(navigationAnimatorStackRepository)
    }

    @Test
    fun `invoke triggers notifyTransitionCompleted`() = coroutineTestScope.run {
        everySuspend { navigationAnimatorStackRepository.notifyTransitionCompleted() } returns Unit

        sut.invoke(Unit)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            navigationAnimatorStackRepository.notifyTransitionCompleted()
        }
    }
}
