package com.tezov.tuucho.core.domain.business.interaction.actionMiddleware

import com.tezov.tuucho.core.domain.business.mock.CoroutineTestScope
import com.tezov.tuucho.core.domain.business.mock.MockActionMiddleware
import com.tezov.tuucho.core.domain.business.mock.MockMiddlewareExecutor
import com.tezov.tuucho.core.domain.business.mock.SpyMiddlewareNext
import com.tezov.tuucho.core.domain.business.model.action.ActionModel
import com.tezov.tuucho.core.domain.business.protocol.ActionMiddlewareProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLock
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockType
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockable
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.ProcessActionUseCase
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.MokkeryMatcherScope
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifyNoMoreCalls
import dev.mokkery.verifySuspend
import kotlinx.serialization.json.buildJsonObject
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class ActionExecutorTest {
    private val coroutineTestScope = CoroutineTestScope()
    private lateinit var middlewareExecutor: MockMiddlewareExecutor

    private lateinit var interactionLockResolver: InteractionLockProtocol.Resolver
    private lateinit var interactionLockRegistry: InteractionLockProtocol.Registry
    private lateinit var middlewareFirst: MockActionMiddleware
    private lateinit var middlewareSecond: MockActionMiddleware
    private lateinit var middlewareThird: MockActionMiddleware
    private lateinit var sut: ActionExecutor

    @BeforeTest
    fun setup() {
        coroutineTestScope.setup()
        middlewareExecutor = MockMiddlewareExecutor()
        interactionLockResolver = mock()
        interactionLockRegistry = mock()
        middlewareFirst = MockActionMiddleware("first")
        middlewareSecond = MockActionMiddleware("second")
        middlewareThird = MockActionMiddleware("third")

        sut = ActionExecutor(
            coroutineScopes = coroutineTestScope.mock,
            middlewareExecutor = middlewareExecutor,
            middlewares = listOf(middlewareFirst, middlewareSecond, middlewareThird),
            interactionLockResolver = interactionLockResolver,
            interactionLockRegistry = interactionLockRegistry
        )
    }

    @AfterTest
    fun tearDown() {
        coroutineTestScope.verifyNoMoreCalls()
        verifyNoMoreCalls(
            interactionLockResolver,
            interactionLockRegistry
        )
    }

    fun MokkeryMatcherScope.matchesCommand(
        command: String
    ) = matches<ActionMiddlewareProtocol.Context> {
        it.actionModel.command == command
    }

    @Test
    fun `process JsonElement with no accepting middleware returns null`() = coroutineTestScope.run {
        val actionModel = ActionModel.from("cmd://auth/target")

        val spy = SpyMiddlewareNext.create<ActionMiddlewareProtocol.Context>()
        middlewareFirst._accept = false
        middlewareFirst.spy = spy

        middlewareSecond._accept = false
        middlewareSecond.spy = spy

        middlewareThird._accept = false
        middlewareThird.spy = spy

        val input = ProcessActionUseCase.Input.create(
            route = null,
            model = actionModel,
        )

        sut.process(input)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineTestScope.mock.default.withContext<Any>(any())
        }
        verifyNoMoreCalls(spy)
    }

    @Test
    fun `process JsonElement with one accepting middleware is executed`() = coroutineTestScope.run {
        val actionModel = ActionModel.from("cmd://auth/target")

        val existingLock = InteractionLock("req", "existing", InteractionLockType.Navigation)
        val inputLockable = InteractionLockable.Lock(listOf(existingLock))
        val lockTypesForAction = InteractionLockable.Type(listOf(InteractionLockType.Screen))

        val newLock = InteractionLock("req", "new", InteractionLockType.Screen)
        val acquiredLockable = InteractionLockable.Lock(listOf(existingLock, newLock))

        val spy = SpyMiddlewareNext.create<ActionMiddlewareProtocol.Context>()
        middlewareFirst._priority = ActionMiddlewareProtocol.Priority.DEFAULT
        middlewareFirst._accept = true
        middlewareFirst.spy = spy

        middlewareSecond._accept = false
        middlewareThird._accept = false

        every { interactionLockRegistry.lockTypeFor(actionModel.command, actionModel.authority) } returns lockTypesForAction
        everySuspend { interactionLockResolver.acquire(any(), any()) } returns acquiredLockable
        everySuspend { interactionLockResolver.release(any(), any()) } returns Unit

        val input = ProcessActionUseCase.Input.create(
            route = null,
            model = actionModel,
            lockable = inputLockable
        )

        sut.process(input)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineTestScope.mock.default.withContext<Any>(any())
            interactionLockRegistry.lockTypeFor(actionModel.command, actionModel.authority)
            interactionLockResolver.acquire(any(), any())
            spy.invoke(matches {
                it.actionModel.command == "first"
            })
            interactionLockResolver.release(any(), acquiredLockable)
        }
        verifyNoMoreCalls(spy)
    }

    @Test
    fun `process JsonElement calls accepting middlewares sorted by priority`() = coroutineTestScope.run {
        val actionModel = ActionModel.from("cmd://auth/target")

        val spy = SpyMiddlewareNext.create<ActionMiddlewareProtocol.Context>()
        middlewareFirst._priority = ActionMiddlewareProtocol.Priority.DEFAULT
        middlewareFirst._accept = true
        middlewareFirst.spy = spy

        middlewareSecond._priority = ActionMiddlewareProtocol.Priority.HIGH
        middlewareSecond._accept = true
        middlewareSecond.spy = spy

        middlewareThird._priority = ActionMiddlewareProtocol.Priority.LOW
        middlewareThird._accept = true
        middlewareThird.spy = spy

        every { interactionLockRegistry.lockTypeFor(any(), any()) } returns InteractionLockable.Empty
        everySuspend { interactionLockResolver.acquire(any(), any()) } returns InteractionLockable.Empty
        everySuspend { interactionLockResolver.release(any(), any()) } returns Unit

        val input = ProcessActionUseCase.Input.create(
            route = null,
            model = actionModel,
            lockable = null
        )

        sut.process(input)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineTestScope.mock.default.withContext<Any>(any())
            interactionLockRegistry.lockTypeFor(actionModel.command, actionModel.authority)
            interactionLockResolver.acquire(any(), any())
            spy.invoke(matchesCommand("second"))
            spy.invoke(matchesCommand("first"))
            spy.invoke(matchesCommand("third"))
            interactionLockResolver.release(any(), InteractionLockable.Empty)
        }
        verifyNoMoreCalls(spy)
    }

    @Test
    fun `process ActionObject with empty primary returns null`() = coroutineTestScope.run {
        val actionObject = buildJsonObject { }

        val input = ProcessActionUseCase.Input.create(
            route = null,
            modelObject = actionObject,
            lockable = InteractionLockable.Empty
        )

        sut.process(input)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineTestScope.mock.default.withContext<Any>(any())
        }
    }
}
