package com.tezov.tuucho.core.domain.business.interaction.actionMiddleware

import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.mock.MockMiddlewareNext
import com.tezov.tuucho.core.domain.business.mock.SpyMiddlewareNext
import com.tezov.tuucho.core.domain.business.model.action.ActionModel
import com.tezov.tuucho.core.domain.business.protocol.ActionMiddlewareProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseExecutorProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockable
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.NavigateToUrlUseCase
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.ProcessActionUseCase
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifyNoMoreCalls
import dev.mokkery.verifySuspend
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
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
        assertEquals(ActionMiddlewareProtocol.Priority.DEFAULT, sut.priority)
    }

    @Test
    fun `accept matches only NavigateAction Url`() {
        val valid = ActionModel.from("navigate://url/whatever")
        val invalidCmd = ActionModel.from("x://url/t")
        val invalidAuth = ActionModel.from("navigate://xxx/t")

        assertTrue(sut.accept(null, valid))
        assertFalse(sut.accept(null, invalidCmd))
        assertFalse(sut.accept(null, invalidAuth))
    }

    @Test
    fun `process calls NavigateToUrlUseCase then next`() = runTest {
        val action = ActionModel.from("navigate://url/final")

        val context = ActionMiddlewareProtocol.Context(
            lockable = InteractionLockable.Empty,
            actionModel = action,
            input = ProcessActionUseCase.Input.create(
                route = NavigationRoute.Back,
                model = action,
                lockable = InteractionLockable.Empty
            )
        )

        val spy = SpyMiddlewareNext.create<ActionMiddlewareProtocol.Context>()
        val next = MockMiddlewareNext<ActionMiddlewareProtocol.Context, Unit>(spy)
        everySuspend { useCaseExecutor.await<NavigateToUrlUseCase.Input, Unit>(any(), any()) } returns Unit

        flow { sut.run { process(context, next) } }.collect()

        verifySuspend(VerifyMode.exhaustiveOrder) {
            useCaseExecutor.await(
                useCase = navigateToUrl,
                input = NavigateToUrlUseCase.Input(url = "final")
            )
            spy.invoke(context)
        }
        verifyNoMoreCalls(spy)
    }

    @Test
    fun `process skips NavigateToUrlUseCase when no target but still calls next`() = runTest {
        val action = ActionModel.from("navigate://url")

        val context = ActionMiddlewareProtocol.Context(
            lockable = InteractionLockable.Empty,
            actionModel = action,
            input = ProcessActionUseCase.Input.create(
                route = NavigationRoute.Back,
                model = action,
                lockable = InteractionLockable.Empty
            )
        )

        val spy = SpyMiddlewareNext.create<ActionMiddlewareProtocol.Context>()
        val next = MockMiddlewareNext<ActionMiddlewareProtocol.Context, Unit>(spy)

        flow { sut.run { process(context, next) } }.collect()

        verify(VerifyMode.exhaustiveOrder) {
            spy.invoke(context)
        }
        verifyNoMoreCalls(spy)
    }

    @Test
    fun `process invokes NavigateToUrlUseCase and completes when next is null`() = runTest {
        val action = ActionModel.from("navigate://url/final")

        val context = ActionMiddlewareProtocol.Context(
            lockable = InteractionLockable.Empty,
            actionModel = action,
            input = ProcessActionUseCase.Input.create(
                route = NavigationRoute.Current,
                model = action,
                lockable = InteractionLockable.Empty
            )
        )

        everySuspend { useCaseExecutor.await<NavigateToUrlUseCase.Input, Unit>(any(), any()) } returns Unit

        flow { sut.run { process(context, null) } }.collect()

        verifySuspend(VerifyMode.exhaustiveOrder) {
            useCaseExecutor.await(
                useCase = navigateToUrl,
                input = NavigateToUrlUseCase.Input(url = "final")
            )
        }
    }
}
