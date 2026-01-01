package com.tezov.tuucho.core.domain.business.interaction.actionMiddleware

import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.response.FormSendSchema
import com.tezov.tuucho.core.domain.business.middleware.ActionMiddleware
import com.tezov.tuucho.core.domain.business.model.ActionModelDomain
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseExecutorProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockable
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.ProcessActionUseCase
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.UpdateViewUseCase
import dev.mokkery.answering.calls
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifyNoMoreCalls
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FormUpdateActionMiddlewareTest {
    private lateinit var useCaseExecutor: UseCaseExecutorProtocol
    private lateinit var updateViewUseCase: UpdateViewUseCase
    private lateinit var sut: FormUpdateActionMiddleware

    @BeforeTest
    fun setup() {
        useCaseExecutor = mock()
        updateViewUseCase = mock()
        sut = FormUpdateActionMiddleware(
            useCaseExecutor = useCaseExecutor,
            updateView = updateViewUseCase
        )
    }

    @AfterTest
    fun tearDown() {
        verifyNoMoreCalls(
            useCaseExecutor,
            updateViewUseCase
        )
    }

    @Test
    fun `priority returns DEFAULT`() {
        assertEquals(ActionMiddleware.Priority.DEFAULT, sut.priority)
    }

    @Test
    fun `accept matches only form update`() {
        val valid = ActionModelDomain.from("form://update/error")
        val wrongCommand = ActionModelDomain.from("x://update/error")
        val wrongAuthority = ActionModelDomain.from("form://other/error")

        assertTrue(sut.accept(null, valid))
        assertFalse(sut.accept(null, wrongCommand))
        assertFalse(sut.accept(null, wrongAuthority))
    }

    @Test
    fun `process returns when route is null and calls next only`() = runTest {
        val action = ActionModelDomain.from("form://update/error")
        val jsonArray = JsonArray(listOf(JsonPrimitive("fieldId")))

        val context = ActionMiddleware.Context(
            lockable = InteractionLockable.Empty,
            input = ProcessActionUseCase.Input.JsonElement(
                route = null,
                action = action,
                lockable = InteractionLockable.Empty,
                jsonElement = jsonArray
            )
        )

        val next = mock<MiddlewareProtocol.Next<ActionMiddleware.Context, ProcessActionUseCase.Output>>()

        everySuspend { next.invoke(any()) } returns ProcessActionUseCase.Output.ElementArray(emptyList())

        sut.process(context, next)

        verifySuspend {
            next.invoke(context)
        }
    }

    @Test
    fun `process error target updates view for each param and calls next`() = runTest {
        val action = ActionModelDomain.from("form://update/error")
        val jsonArray = buildJsonArray {
            add(JsonPrimitive("fieldA"))
            add(JsonPrimitive("fieldB"))
        }

        val route = NavigationRoute.Url(id = "id", value = "url")

        val context = ActionMiddleware.Context(
            lockable = InteractionLockable.Empty,
            input = ProcessActionUseCase.Input.JsonElement(
                route = route,
                action = action,
                lockable = InteractionLockable.Empty,
                jsonElement = jsonArray
            )
        )

        val next = mock<MiddlewareProtocol.Next<ActionMiddleware.Context, ProcessActionUseCase.Output>>()

        everySuspend { useCaseExecutor.await<UpdateViewUseCase.Input, Unit>(any(), any()) } returns Unit
        everySuspend { next.invoke(any()) } returns ProcessActionUseCase.Output.ElementArray(emptyList())

        sut.process(context, next)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            useCaseExecutor.await(
                useCase = updateViewUseCase,
                input = any()
            )
            next.invoke(context)
        }
    }

    @Test
    fun `process error target with reason sets messageErrorExtra and works when next is null`() = runTest {
        val action = ActionModelDomain.from("form://update/error")
        val failureParam = JsonNull
            .withScope(FormSendSchema.FailureResult::Scope)
            .apply {
                reason = buildJsonObject { put("code", "error-code") }
            }.collect()
        val jsonArray = buildJsonArray {
            add(failureParam)
        }

        val route = NavigationRoute.Url(id = "id", value = "url")

        val capturedInputs = mutableListOf<UpdateViewUseCase.Input>()

        val context = ActionMiddleware.Context(
            lockable = InteractionLockable.Empty,
            input = ProcessActionUseCase.Input.JsonElement(
                route = route,
                action = action,
                lockable = InteractionLockable.Empty,
                jsonElement = jsonArray
            )
        )

        everySuspend {
            useCaseExecutor.await<UpdateViewUseCase.Input, Unit>(any(), any())
        } calls { args ->
            val input = args.arg<UpdateViewUseCase.Input>(1)
            capturedInputs.add(input)
            Unit
        }

        sut.process(context, null)

        assertEquals(1, capturedInputs.size)

        verifySuspend {
            useCaseExecutor.await(
                useCase = updateViewUseCase,
                input = any()
            )
        }
    }

    @Test
    fun `process error target with null jsonElement skips update and calls next`() = runTest {
        val action = ActionModelDomain.from("form://update/error")
        val route = NavigationRoute.Url(id = "id", value = "url")

        val context = ActionMiddleware.Context(
            lockable = InteractionLockable.Empty,
            input = ProcessActionUseCase.Input.JsonElement(
                route = route,
                action = action,
                lockable = InteractionLockable.Empty,
                jsonElement = null
            )
        )

        val next = mock<MiddlewareProtocol.Next<ActionMiddleware.Context, ProcessActionUseCase.Output>>()
        everySuspend { next.invoke(any()) } returns ProcessActionUseCase.Output.ElementArray(emptyList())

        sut.process(context, next)

        verifySuspend {
            next.invoke(context)
        }
    }

    @Test
    fun `process throws for unknown target`() = runTest {
        val action = ActionModelDomain.from("form://update/unknown")
        val route = NavigationRoute.Url(id = "id", value = "url")

        val context = ActionMiddleware.Context(
            lockable = InteractionLockable.Empty,
            input = ProcessActionUseCase.Input.JsonElement(
                route = route,
                action = action,
                lockable = InteractionLockable.Empty,
                jsonElement = JsonNull
            )
        )

        assertFailsWith<DomainException> {
            sut.process(context, null)
        }
    }

    @Test
    fun `process returns early when route is null and invokes next`() = runTest {
        val action = ActionModelDomain.from("form://update/error")

        val context = ActionMiddleware.Context(
            lockable = InteractionLockable.Empty,
            input = ProcessActionUseCase.Input.JsonElement(
                route = null,
                action = action,
                lockable = InteractionLockable.Empty,
                jsonElement = null
            )
        )

        val next = mock<MiddlewareProtocol.Next<ActionMiddleware.Context, ProcessActionUseCase.Output>>()

        everySuspend { next.invoke(any()) } returns ProcessActionUseCase.Output.ElementArray(emptyList())

        sut.process(context, next)

        verifySuspend {
            next.invoke(context)
        }
    }
}
