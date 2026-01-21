package com.tezov.tuucho.core.domain.business.interaction.actionMiddleware

import com.tezov.tuucho.core.domain.business.middleware.ActionMiddleware
import com.tezov.tuucho.core.domain.business.middleware.ActionMiddleware.Context
import com.tezov.tuucho.core.domain.business.mock.CoroutineTestScope
import com.tezov.tuucho.core.domain.business.model.ActionModelDomain
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareExecutorProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLock
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockType
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockable
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.ProcessActionUseCase
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.matcher.matches
import dev.mokkery.mock
import dev.mokkery.verify
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verify.VerifyMode.Companion.atLeast
import dev.mokkery.verifyNoMoreCalls
import dev.mokkery.verifySuspend
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class ActionExecutorTest {
    private val coroutineTestScope = CoroutineTestScope()
    private lateinit var interactionLockResolver: InteractionLockProtocol.Resolver
    private lateinit var interactionLockRegistry: InteractionLockProtocol.Registry
    private lateinit var middlewareExecutor: MiddlewareExecutorProtocol
    private lateinit var middlewareFirst: ActionMiddleware
    private lateinit var middlewareSecond: ActionMiddleware
    private lateinit var middlewareThird: ActionMiddleware
    private lateinit var sut: ActionExecutor

    @BeforeTest
    fun setup() {
        coroutineTestScope.setup()
        interactionLockResolver = mock()
        interactionLockRegistry = mock()
        middlewareExecutor = mock()
        middlewareFirst = mock()
        middlewareSecond = mock()
        middlewareThird = mock()

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
            interactionLockRegistry,
            middlewareExecutor,
            middlewareFirst,
            middlewareSecond,
            middlewareThird
        )
    }

    @Test
    fun `process JsonElement with no accepting middleware returns null`() = coroutineTestScope.run {
        val actionModel = ActionModelDomain.from("cmd://auth/target")

        every { middlewareFirst.accept(null, actionModel) } returns false
        every { middlewareSecond.accept(null, actionModel) } returns false
        every { middlewareThird.accept(null, actionModel) } returns false

        val input = ProcessActionUseCase.Input.Action(
            route = null,
            action = actionModel,
            lockable = null,
            jsonElement = JsonPrimitive("ignored")
        )

        val result = sut.process(input)
        assertNull(result)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineTestScope.mock.action.await<Any>(any())
            middlewareFirst.accept(null, actionModel)
            middlewareSecond.accept(null, actionModel)
            middlewareThird.accept(null, actionModel)
        }
    }

    @Test
    fun `process JsonElement with one accepting middleware returns its output`() = coroutineTestScope.run {
        val actionModel = ActionModelDomain.from("cmd://auth/target")

        val existingLock = InteractionLock("req", "existing", InteractionLockType.Navigation)
        val inputLockable = InteractionLockable.Lock(listOf(existingLock))
        val lockTypesForAction = InteractionLockable.Type(listOf(InteractionLockType.Screen))

        val newLock = InteractionLock("req", "new", InteractionLockType.Screen)
        val acquiredLockable = InteractionLockable.Lock(listOf(existingLock, newLock))

        every { middlewareFirst.accept(null, actionModel) } returns true
        every { middlewareSecond.accept(null, actionModel) } returns false
        every { middlewareThird.accept(null, actionModel) } returns false

        every { middlewareFirst.priority } returns ActionMiddleware.Priority.DEFAULT

        every { interactionLockRegistry.lockTypeFor(actionModel.command, actionModel.authority) } returns lockTypesForAction
        everySuspend { interactionLockResolver.acquire(any(), any()) } returns acquiredLockable
        everySuspend { interactionLockResolver.release(any(), any()) } returns Unit

        val input = ProcessActionUseCase.Input.Action(
            route = null,
            action = actionModel,
            lockable = inputLockable,
            jsonElement = JsonPrimitive("payload")
        )
        val expectedOutput = ProcessActionUseCase.Output.Element(String::class, "result")
        everySuspend {
            middlewareExecutor.process<Context, ProcessActionUseCase.Output>(any(), any())
        } returns expectedOutput

        val result = sut.process(input)
        assertSame(expectedOutput, result)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineTestScope.mock.action.await<Any>(any())
            middlewareFirst.accept(null, actionModel)
            middlewareSecond.accept(null, actionModel)
            middlewareThird.accept(null, actionModel)
            interactionLockRegistry.lockTypeFor(actionModel.command, actionModel.authority)
            interactionLockResolver.acquire(any(), any())
            middlewareExecutor.process<Context, ProcessActionUseCase.Output>(
                matches { list ->
                    list.size == 1 && list.first() == middlewareFirst
                },
                matches { context ->
                    context.input == input &&
                        context.lockable is InteractionLockable.Lock &&
                        context.lockable.getLocks().all { !it.canBeReleased }
                }
            )
            interactionLockResolver.release(any(), acquiredLockable)
        }
    }

    @Test
    fun `process JsonElement calls accepting middlewares sorted by priority`() = coroutineTestScope.run {
        val actionModel = ActionModelDomain.from("cmd://auth/target")

        every { middlewareFirst.priority } returns ActionMiddleware.Priority.DEFAULT
        every { middlewareSecond.priority } returns ActionMiddleware.Priority.HIGH
        every { middlewareThird.priority } returns ActionMiddleware.Priority.LOW

        every { middlewareFirst.accept(null, actionModel) } returns true
        every { middlewareSecond.accept(null, actionModel) } returns true
        every { middlewareThird.accept(null, actionModel) } returns true

        every { interactionLockRegistry.lockTypeFor(any(), any()) } returns InteractionLockable.Empty
        everySuspend { interactionLockResolver.acquire(any(), any()) } returns InteractionLockable.Empty
        everySuspend { interactionLockResolver.release(any(), any()) } returns Unit

        val output = ProcessActionUseCase.Output.Element(String::class, "output")

        everySuspend {
            middlewareExecutor.process<Context, ProcessActionUseCase.Output>(any(), any())
        } returns output

        val input = ProcessActionUseCase.Input.Action(
            route = null,
            action = actionModel,
            lockable = null,
            jsonElement = JsonPrimitive("payload")
        )

        sut.process(input)

        verify(atLeast(2)) {
            middlewareFirst.priority
            middlewareSecond.priority
            middlewareThird.priority
        }

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineTestScope.mock.action.await<Any>(any())
            middlewareFirst.accept(null, actionModel)
            middlewareSecond.accept(null, actionModel)
            middlewareThird.accept(null, actionModel)
            interactionLockRegistry.lockTypeFor(actionModel.command, actionModel.authority)
            interactionLockResolver.acquire(any(), any())
            middlewareExecutor.process<Context, ProcessActionUseCase.Output>(
                matches { list ->
                    list.size == 3 &&
                        list[2] == middlewareThird &&
                        list[1] == middlewareFirst &&
                        list[0] == middlewareSecond
                },
                matches { context ->
                    context.input == input &&
                        context.lockable is InteractionLockable.Empty
                }
            )
            interactionLockResolver.release(any(), InteractionLockable.Empty)
        }
    }

    @Test
    fun `process ActionObject with empty primary returns null`() = coroutineTestScope.run {
        val actionObject = buildJsonObject { }

        val input = ProcessActionUseCase.Input.ActionObject(
            route = null,
            actionObject = actionObject,
            lockable = InteractionLockable.Empty
        )

        val result = sut.process(input)
        assertNull(result)
    }

    @Test
    fun `process ActionObject with one primary returns Element wrapping single output`() = coroutineTestScope.run {
        val actionString = "cmd://auth/one"
        val actionModel = ActionModelDomain.from(actionString)

        val actionObject = buildJsonObject {
            put("primaries", JsonArray(listOf(JsonPrimitive(actionString))))
        }

        every { middlewareFirst.accept(null, actionModel) } returns true
        every { middlewareSecond.accept(null, actionModel) } returns false
        every { middlewareThird.accept(null, actionModel) } returns false

        every { middlewareFirst.priority } returns ActionMiddleware.Priority.DEFAULT
        every { middlewareSecond.priority } returns ActionMiddleware.Priority.DEFAULT
        every { middlewareThird.priority } returns ActionMiddleware.Priority.DEFAULT

        every { interactionLockRegistry.lockTypeFor(any(), any()) } returns InteractionLockable.Empty
        everySuspend { interactionLockResolver.acquire(any(), any()) } returns InteractionLockable.Empty
        everySuspend { interactionLockResolver.release(any(), any()) } returns Unit

        val expectedOutput = ProcessActionUseCase.Output.Element(String::class, "one")

        everySuspend {
            middlewareExecutor.process<Context, ProcessActionUseCase.Output>(any(), any())
        } returns expectedOutput

        val input = ProcessActionUseCase.Input.ActionObject(
            route = null,
            actionObject = actionObject,
            lockable = InteractionLockable.Empty
        )

        val result = sut.process(input)
        val element = result as ProcessActionUseCase.Output.Element
        assertSame(expectedOutput.rawValue, element.rawValue)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineTestScope.mock.action.await<Any>(any())
            middlewareFirst.accept(null, actionModel)
            middlewareSecond.accept(null, actionModel)
            middlewareThird.accept(null, actionModel)
            interactionLockRegistry.lockTypeFor(actionModel.command, actionModel.authority)
            interactionLockResolver.acquire(any(), InteractionLockable.Empty)
            middlewareExecutor.process<Context, ProcessActionUseCase.Output>(
                matches { list ->
                    list.size == 1 && list.first() == middlewareFirst
                },
                matches { context ->
                    context.input.route == null &&
                        context.input.action == actionModel &&
                        context.lockable is InteractionLockable.Empty
                }
            )
            interactionLockResolver.release(any(), InteractionLockable.Empty)
        }
    }

    @Test
    fun `process ActionObject with multiple primary returns ElementArray`() = coroutineTestScope.run {
        val actionStringFirst = "cmd://auth/first"
        val actionStringSecond = "cmd://auth/second"

        val actionModelFirst = ActionModelDomain.from(actionStringFirst)
        val actionModelSecond = ActionModelDomain.from(actionStringSecond)

        val actionObject = buildJsonObject {
            put(
                "primaries",
                JsonArray(
                    listOf(
                        JsonPrimitive(actionStringFirst),
                        JsonPrimitive(actionStringSecond)
                    )
                )
            )
        }

        every { middlewareFirst.priority } returns ActionMiddleware.Priority.DEFAULT
        every { middlewareSecond.priority } returns ActionMiddleware.Priority.DEFAULT
        every { middlewareThird.priority } returns ActionMiddleware.Priority.DEFAULT

        every { interactionLockRegistry.lockTypeFor(any(), any()) } returns InteractionLockable.Empty
        everySuspend { interactionLockResolver.acquire(any(), any()) } returns InteractionLockable.Empty
        everySuspend { interactionLockResolver.release(any(), any()) } returns Unit

        every { middlewareFirst.accept(null, actionModelFirst) } returns true
        every { middlewareSecond.accept(null, actionModelFirst) } returns false
        every { middlewareThird.accept(null, actionModelFirst) } returns false

        every { middlewareFirst.accept(null, actionModelSecond) } returns false
        every { middlewareSecond.accept(null, actionModelSecond) } returns true
        every { middlewareThird.accept(null, actionModelSecond) } returns false

        everySuspend {
            middlewareExecutor.process(
                matches<List<ActionMiddleware>> {
                    it.size == 1 && it.first() == middlewareFirst
                },
                any()
            )
        } returns ProcessActionUseCase.Output.Element(Any::class, "one")

        everySuspend {
            middlewareExecutor.process(
                matches<List<ActionMiddleware>> {
                    it.size == 1 && it.first() == middlewareSecond
                },
                any()
            )
        } returns ProcessActionUseCase.Output.Element(Any::class, "two")

        val input = ProcessActionUseCase.Input.ActionObject(
            route = null,
            actionObject = actionObject,
            lockable = InteractionLockable.Empty
        )

        val result = sut.process(input)
        assertTrue(result is ProcessActionUseCase.Output.ElementArray)
        result.values.forEach {
            assertTrue(it is ProcessActionUseCase.Output.Element)
        }

        @Suppress("UNCHECKED_CAST")
        val elements = result.values as List<ProcessActionUseCase.Output.Element>
        assertEquals("one", elements[0].value<String>())
        assertEquals("two", elements[1].value<String>())

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineTestScope.mock.action.await<Any>(any())
            middlewareFirst.accept(null, actionModelFirst)
            middlewareSecond.accept(null, actionModelFirst)
            middlewareThird.accept(null, actionModelFirst)
            interactionLockRegistry.lockTypeFor(actionModelFirst.command, actionModelFirst.authority)
            interactionLockResolver.acquire(any(), any())
            middlewareExecutor.process<Context, ProcessActionUseCase.Output>(any(), any())
            interactionLockResolver.release(any(), InteractionLockable.Empty)

            coroutineTestScope.mock.action.await<Any>(any())
            middlewareFirst.accept(null, actionModelSecond)
            middlewareSecond.accept(null, actionModelSecond)
            middlewareThird.accept(null, actionModelSecond)
            interactionLockRegistry.lockTypeFor(actionModelSecond.command, actionModelSecond.authority)
            interactionLockResolver.acquire(any(), any())
            middlewareExecutor.process<Context, ProcessActionUseCase.Output>(any(), any())
            interactionLockResolver.release(any(), InteractionLockable.Empty)
        }
    }
}
