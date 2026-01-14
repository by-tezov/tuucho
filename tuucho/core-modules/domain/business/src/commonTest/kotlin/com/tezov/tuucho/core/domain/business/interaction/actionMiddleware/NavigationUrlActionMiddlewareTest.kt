package com.tezov.tuucho.core.domain.business.interaction.actionMiddleware

import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.middleware.ActionMiddleware
import com.tezov.tuucho.core.domain.business.model.ActionModelDomain
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseExecutorProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockable
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.NavigateToUrlUseCase
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
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class NavigationUrlActionMiddlewareTest {
    private lateinit var useCaseExecutor: UseCaseExecutorProtocol
    private lateinit var navigateToUrl: NavigateToUrlUseCase
    private lateinit var sut: NavigationUrlActionMiddleware

    @BeforeTest
    fun setup() {
        useCaseExecutor = mock()
        navigateToUrl = mock()
        sut = NavigationUrlActionMiddleware(
            useCaseExecutor = useCaseExecutor,
            navigateToUrl = navigateToUrl
        )
    }

    @AfterTest
    fun tearDown() {
        verifyNoMoreCalls(
            useCaseExecutor,
            navigateToUrl
        )
    }

    @Test
    fun `priority returns DEFAULT`() {
        assertEquals(ActionMiddleware.Priority.DEFAULT, sut.priority)
    }

    @Test
    fun `accept matches only NavigateAction Url`() {
        val valid = ActionModelDomain.from("navigate://url/whatever")
        val invalidCmd = ActionModelDomain.from("x://url/t")
        val invalidAuth = ActionModelDomain.from("navigate://xxx/t")

        assertTrue(sut.accept(null, valid))
        assertFalse(sut.accept(null, invalidCmd))
        assertFalse(sut.accept(null, invalidAuth))
    }

    @Test
    fun `process calls NavigateToUrlUseCase then next`() = runTest {
        val action = ActionModelDomain.from("navigate://url/final")

        val context = ActionMiddleware.Context(
            lockable = InteractionLockable.Empty,
            input = ProcessActionUseCase.Input.Action(
                route = NavigationRoute.Back,
                action = action,
                lockable = InteractionLockable.Empty
            )
        )

        val next = mock<MiddlewareProtocol.Next<ActionMiddleware.Context, ProcessActionUseCase.Output>>()

        everySuspend { next.invoke(any()) } returns ProcessActionUseCase.Output.ElementArray(emptyList())
        everySuspend { useCaseExecutor.await<NavigateToUrlUseCase.Input, Unit>(any(), any()) } returns Unit

        sut.process(context, next)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            useCaseExecutor.await(
                useCase = navigateToUrl,
                input = NavigateToUrlUseCase.Input(url = "final")
            )
            next.invoke(context)
        }
    }

    @Test
    fun `process skips NavigateToUrlUseCase when no target but still calls next`() = runTest {
        val action = ActionModelDomain.from("navigate://url")

        val context = ActionMiddleware.Context(
            lockable = InteractionLockable.Empty,
            input = ProcessActionUseCase.Input.Action(
                route = NavigationRoute.Back,
                action = action,
                lockable = InteractionLockable.Empty
            )
        )

        val next = mock<MiddlewareProtocol.Next<ActionMiddleware.Context, ProcessActionUseCase.Output>>()

        everySuspend { next.invoke(any()) } returns ProcessActionUseCase.Output.ElementArray(emptyList())

        sut.process(context, next)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            next.invoke(context)
        }
    }

    @Test
    fun `process invokes NavigateToUrlUseCase and completes when next is null`() = runTest {
        val action = ActionModelDomain.from("navigate://url/final")

        val context = ActionMiddleware.Context(
            lockable = InteractionLockable.Empty,
            input = ProcessActionUseCase.Input.Action(
                route = NavigationRoute.Current,
                action = action,
                lockable = InteractionLockable.Empty
            )
        )

        everySuspend { useCaseExecutor.await<NavigateToUrlUseCase.Input, Unit>(any(), any()) } returns Unit

        sut.process(context, null)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            useCaseExecutor.await(
                useCase = navigateToUrl,
                input = NavigateToUrlUseCase.Input(url = "final")
            )
        }
    }
}
