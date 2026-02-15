package com.tezov.tuucho.core.domain.business.interaction.actionMiddleware

import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.mock.middleware.MockMiddlewareNext
import com.tezov.tuucho.core.domain.business.mock.middleware.SpyMiddlewareNext
import com.tezov.tuucho.core.domain.business.model.action.ActionModel
import com.tezov.tuucho.core.domain.business.protocol.ActionMiddlewareProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseExecutorProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockable
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.NavigateBackUseCase
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.ProcessActionUseCase
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.NavigateFinishUseCase
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
    private lateinit var navigateFinish: NavigateFinishUseCase
    private lateinit var sut: NavigationLocalDestinationActionMiddleware

    @BeforeTest
    fun setup() {
        useCaseExecutor = mock()
        navigateBack = mock()
        navigateFinish = mock()
        sut = NavigationLocalDestinationActionMiddleware(
            useCaseExecutor = useCaseExecutor,
            navigateBack = navigateBack,
            navigateFinish = navigateFinish
        )
    }

    @AfterTest
    fun tearDown() {
        verifyNoMoreCalls(
            useCaseExecutor,
            navigateBack,
            navigateFinish
        )
    }

    @Test
    fun `priority returns DEFAULT`() {
        assertEquals(ActionMiddlewareProtocol.Priority.DEFAULT, sut.priority)
    }

    @Test
    fun `accept matches only LocalDestination`() {
        val valid = ActionModel.from("navigate://local-destination/back")
        val invalidCmd = ActionModel.from("x://local-destination/t")
        val invalidAuth = ActionModel.from("navigate://xxx/t")

        assertTrue(sut.accept(null, valid))
        assertFalse(sut.accept(null, invalidCmd))
        assertFalse(sut.accept(null, invalidAuth))
    }

    @Test
    fun `process triggers NavigateBackUseCase then next`() = runTest {
        val action = ActionModel.from("navigate://local-destination/back")

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
        val next = MockMiddlewareNext(spy)
        everySuspend { useCaseExecutor.await<NavigateBackUseCase, Unit>(any(), any()) } returns Unit

        sut.process(context, next)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            useCaseExecutor.await(
                useCase = navigateBack,
                input = Unit
            )
            spy.invoke(context)
        }
        verifyNoMoreCalls(spy)
    }

    @Test
    fun `process triggers NavigateFinishUseCase then next`() = runTest {
        val action = ActionModel.from("navigate://local-destination/finish")

        val context = ActionMiddlewareProtocol.Context(
            lockable = InteractionLockable.Empty,
            actionModel = action,
            input = ProcessActionUseCase.Input.create(
                route = NavigationRoute.Finish,
                model = action,
                lockable = InteractionLockable.Empty
            )
        )

        val spy = SpyMiddlewareNext.create<ActionMiddlewareProtocol.Context>()
        val next = MockMiddlewareNext(spy)
        everySuspend { useCaseExecutor.await<NavigateFinishUseCase, Unit>(any(), any()) } returns Unit

        sut.process(context, next)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            useCaseExecutor.await(
                useCase = navigateFinish,
                input = Unit
            )
            spy.invoke(context)
        }
        verifyNoMoreCalls(spy)
    }

    @Test
    fun `process throws when target is unknown`() = runTest {
        val action = ActionModel.from("navigate://local-destination/xxx")

        val context = ActionMiddlewareProtocol.Context(
            lockable = InteractionLockable.Empty,
            actionModel = action,
            input = ProcessActionUseCase.Input.create(
                route = NavigationRoute.Back,
                models = listOf(action),
                lockable = InteractionLockable.Empty
            )
        )

        assertFailsWith<DomainException> {
            sut.process(context, null)
        }
    }

    @Test
    fun `process with null next still executes NavigateBack`() = runTest {
        val action = ActionModel.from("navigate://local-destination/back")

        val context = ActionMiddlewareProtocol.Context(
            lockable = InteractionLockable.Empty,
            actionModel = action,
            input = ProcessActionUseCase.Input.create(
                route = NavigationRoute.Back,
                model = action,
                lockable = InteractionLockable.Empty
            )
        )

        everySuspend { useCaseExecutor.await<NavigateBackUseCase, Unit>(any(), any()) } returns Unit

        sut.run { process(context, null) }

        verifySuspend {
            useCaseExecutor.await(
                useCase = navigateBack,
                input = Unit
            )
        }
    }
}
