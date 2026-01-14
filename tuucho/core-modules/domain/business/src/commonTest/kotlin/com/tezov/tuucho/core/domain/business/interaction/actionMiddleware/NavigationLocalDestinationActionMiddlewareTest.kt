package com.tezov.tuucho.core.domain.business.interaction.actionMiddleware

import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.middleware.ActionMiddleware
import com.tezov.tuucho.core.domain.business.model.ActionModelDomain
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseExecutorProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockable
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.NavigateBackUseCase
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.ProcessActionUseCase
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifyNoMoreCalls
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class NavigationLocalDestinationActionMiddlewareTest {
    private lateinit var useCaseExecutor: UseCaseExecutorProtocol
    private lateinit var navigateBack: NavigateBackUseCase
    private lateinit var sut: NavigationLocalDestinationActionMiddleware

    @BeforeTest
    fun setup() {
        useCaseExecutor = mock()
        navigateBack = mock()
        sut = NavigationLocalDestinationActionMiddleware(
            useCaseExecutor = useCaseExecutor,
            navigateBack = navigateBack
        )
    }

    @AfterTest
    fun tearDown() {
        verifyNoMoreCalls(
            useCaseExecutor,
            navigateBack
        )
    }

    @Test
    fun `priority returns DEFAULT`() {
        assertEquals(ActionMiddleware.Priority.DEFAULT, sut.priority)
    }

    @Test
    fun `accept matches only LocalDestination`() {
        val valid = ActionModelDomain.from("navigate://local-destination/back")
        val invalidCmd = ActionModelDomain.from("x://local-destination/t")
        val invalidAuth = ActionModelDomain.from("navigate://xxx/t")

        assertTrue(sut.accept(null, valid))
        assertFalse(sut.accept(null, invalidCmd))
        assertFalse(sut.accept(null, invalidAuth))
    }

    @Test
    fun `process triggers NavigateBackUseCase then next`() = runTest {
        val action = ActionModelDomain.from("navigate://local-destination/back")

        val context = ActionMiddleware.Context(
            lockable = InteractionLockable.Empty,
            input = ProcessActionUseCase.Input.Action(
                route = NavigationRoute.Back,
                action = action,
                lockable = InteractionLockable.Empty
            )
        )

        val next = mock<MiddlewareProtocol.Next<ActionMiddleware.Context, ProcessActionUseCase.Output>>()

        everySuspend { useCaseExecutor.await<NavigateBackUseCase, Unit>(any(), any()) } returns Unit
        everySuspend { next.invoke(any()) } returns ProcessActionUseCase.Output.ElementArray(emptyList())

        sut.process(context, next)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            useCaseExecutor.await(
                useCase = navigateBack,
                input = Unit
            )
            next.invoke(context)
        }
    }

    @Test
    fun `process throws when target is unknown`() = runTest {
        val action = ActionModelDomain.from("navigate://local-destination/xxx")

        val context = ActionMiddleware.Context(
            lockable = InteractionLockable.Empty,
            input = ProcessActionUseCase.Input.Action(
                route = NavigationRoute.Back,
                action = action,
                lockable = InteractionLockable.Empty
            )
        )

        assertFailsWith<DomainException> {
            sut.process(context, null)
        }
    }

    @Test
    fun `process with null next still executes NavigateBack`() = runTest {
        val action = ActionModelDomain.from("navigate://local-destination/back")

        val context = ActionMiddleware.Context(
            lockable = InteractionLockable.Empty,
            input = ProcessActionUseCase.Input.Action(
                route = NavigationRoute.Back,
                action = action,
                lockable = InteractionLockable.Empty
            )
        )

        everySuspend { useCaseExecutor.await<NavigateBackUseCase, Unit>(any(), any()) } returns Unit

        sut.process(context, null)

        verifySuspend {
            useCaseExecutor.await(
                useCase = navigateBack,
                input = Unit
            )
        }
    }
}
