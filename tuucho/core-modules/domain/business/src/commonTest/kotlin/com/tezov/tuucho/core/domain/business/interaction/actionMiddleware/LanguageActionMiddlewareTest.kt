package com.tezov.tuucho.core.domain.business.interaction.actionMiddleware

import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.mock.middleware.MockMiddlewareNext
import com.tezov.tuucho.core.domain.business.mock.middleware.SpyMiddlewareNext
import com.tezov.tuucho.core.domain.business.model.LanguageModelDomain
import com.tezov.tuucho.core.domain.business.model.action.ActionModel
import com.tezov.tuucho.core.domain.business.protocol.ActionMiddlewareProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseExecutorProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockable
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.ProcessActionUseCase
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.SetLanguageUseCase
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
import kotlin.test.assertTrue

class LanguageActionMiddlewareTest {
    private lateinit var useCaseExecutor: UseCaseExecutorProtocol
    private lateinit var setLanguageUseCase: SetLanguageUseCase
    private lateinit var sut: LanguageActionMiddleware

    @BeforeTest
    fun setup() {
        useCaseExecutor = mock()
        setLanguageUseCase = mock()
        sut = LanguageActionMiddleware(
            useCaseExecutor = useCaseExecutor,
            setLanguage = setLanguageUseCase
        )
    }

    @AfterTest
    fun tearDown() {
        verifyNoMoreCalls(
            useCaseExecutor,
            setLanguageUseCase
        )
    }

    @Test
    fun `priority returns DEFAULT`() {
        assertEquals(ActionMiddlewareProtocol.Priority.DEFAULT, sut.priority)
    }

    // **************** Authority Current

    @Test
    fun `accept returns true for current authority`() {
        val action = ActionModel.from("language://current?code=en")

        assertTrue(sut.accept(null, action))
    }

    @Test
    fun `process current sets explicit language then next`() = runTest {
        val action = ActionModel.from("language://current?code=fr&country=FR")

        val context = ActionMiddlewareProtocol.Context(
            lockable = InteractionLockable.Empty,
            actionModel = action,
            input = ProcessActionUseCase.Input.create(
                route = NavigationRoute.Back,
                model = action,
                lockable = InteractionLockable.Empty,
                jsonElement = null
            )
        )

        val spy = SpyMiddlewareNext.create<ActionMiddlewareProtocol.Context>()
        val next = MockMiddlewareNext(spy)
        everySuspend { useCaseExecutor.await<SetLanguageUseCase.Input, Unit>(any(), any()) } returns Unit

        sut.process(context, next)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            useCaseExecutor.await(
                useCase = setLanguageUseCase,
                input = SetLanguageUseCase.Input(
                    LanguageModelDomain(
                        code = "fr",
                        country = "FR"
                    )
                )
            )
            spy.invoke(context)
        }
        verifyNoMoreCalls(spy)
    }

    @Test
    fun `process current throws when query is not object`() = runTest {
        val action = ActionModel.from("language://current?invalid")

        val context = ActionMiddlewareProtocol.Context(
            lockable = InteractionLockable.Empty,
            actionModel = action,
            input = ProcessActionUseCase.Input.create(
                route = NavigationRoute.Back,
                model = action,
                lockable = InteractionLockable.Empty,
                jsonElement = null
            )
        )

        assertFailsWith<DomainException> {
            sut.process(context, null)
        }
    }

    // **************** Authority Language

    @Test
    fun `accept returns true for system authority`() {
        val action = ActionModel.from("language://system")

        assertTrue(sut.accept(null, action))
    }

    @Test
    fun `process system sets system language then next`() = runTest {
        val action = ActionModel.from("language://system")

        val context = ActionMiddlewareProtocol.Context(
            lockable = InteractionLockable.Empty,
            actionModel = action,
            input = ProcessActionUseCase.Input.create(
                route = NavigationRoute.Back,
                model = action,
                lockable = InteractionLockable.Empty,
                jsonElement = null
            )
        )

        val spy = SpyMiddlewareNext.create<ActionMiddlewareProtocol.Context>()
        val next = MockMiddlewareNext(spy)
        everySuspend { useCaseExecutor.await<SetLanguageUseCase.Input, Unit>(any(), any()) } returns Unit

        sut.process(context, next)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            useCaseExecutor.await(
                useCase = setLanguageUseCase,
                input = SetLanguageUseCase.Input(
                    LanguageModelDomain(
                        code = null,
                        country = null
                    )
                )
            )
            spy.invoke(context)
        }
        verifyNoMoreCalls(spy)
    }
}
