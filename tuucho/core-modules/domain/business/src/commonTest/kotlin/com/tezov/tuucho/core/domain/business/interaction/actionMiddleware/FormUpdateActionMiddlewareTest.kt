package com.tezov.tuucho.core.domain.business.interaction.actionMiddleware

import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.jsonSchema._system.withScope
import com.tezov.tuucho.core.domain.business.jsonSchema.response.FormSendSchema
import com.tezov.tuucho.core.domain.business.middleware.ActionMiddleware
import com.tezov.tuucho.core.domain.business.mock.MockMiddlewareNext
import com.tezov.tuucho.core.domain.business.mock.SpyMiddlewareNext
import com.tezov.tuucho.core.domain.business.model.action.ActionModel
import com.tezov.tuucho.core.domain.business.protocol.UseCaseExecutorProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockable
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.ProcessActionUseCase
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.UpdateViewUseCase
import com.tezov.tuucho.core.domain.tool.json.stringOrNull
import dev.mokkery.answering.calls
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.matcher.matches
import dev.mokkery.mock
import dev.mokkery.verify
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifyNoMoreCalls
import dev.mokkery.verifySuspend
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
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
        val valid = ActionModel.from("form://update/error")
        val wrongCommand = ActionModel.from("x://update/error")
        val wrongAuthority = ActionModel.from("form://other/error")

        assertTrue(sut.accept(null, valid))
        assertFalse(sut.accept(null, wrongCommand))
        assertFalse(sut.accept(null, wrongAuthority))
    }

    @Test
    fun `process returns when route is null and calls next only`() = runTest {
        val action = ActionModel.from("form://update/error")

        val context = ActionMiddleware.Context(
            lockable = InteractionLockable.Empty,
            actionModel = action,
            input = ProcessActionUseCase.Input.create(
                route = null,
                model = action,
                lockable = InteractionLockable.Empty
            )
        )

        val spy = SpyMiddlewareNext.create<ActionMiddleware.Context>()
        val next = MockMiddlewareNext<ActionMiddleware.Context, Unit>(spy)

        flow { sut.run { process(context, next) } }.collect()

        verify(VerifyMode.exhaustiveOrder) {
            spy.invoke(context)
        }
        verifyNoMoreCalls(spy)
    }

    @Test
    fun `process error target updates view for each param and calls next`() = runTest {
        val action = ActionModel.from("form://update/error")
        val jsonArray = buildJsonArray {
            add(
                JsonNull
                    .withScope(FormSendSchema.FailureResult::Scope)
                    .apply {
                        reason = buildJsonObject { put("default", "fieldA") }
                    }.collect()
            )
        }

        val route = NavigationRoute.Url(id = "id", value = "url")

        val context = ActionMiddleware.Context(
            lockable = InteractionLockable.Empty,
            actionModel = action,
            input = ProcessActionUseCase.Input.create(
                route = route,
                model = action,
                lockable = InteractionLockable.Empty,
                jsonElement = jsonArray
            )
        )

        val spy = SpyMiddlewareNext.create<ActionMiddleware.Context>()
        val next = MockMiddlewareNext<ActionMiddleware.Context, Unit>(spy)
        everySuspend { useCaseExecutor.await<UpdateViewUseCase.Input, Unit>(any(), any()) } returns Unit

        flow { sut.run { process(context, next) } }.collect()

        verifySuspend(VerifyMode.exhaustiveOrder) {
            useCaseExecutor.await(
                useCase = updateViewUseCase,
                input = matches {
                    val messageErrorExtra = it.jsonObjects.firstNotNullOfOrNull { jsonObject ->
                        jsonObject["message-error-extra"]?.jsonObject["default"]
                    }
                    messageErrorExtra.stringOrNull == "fieldA"
                }
            )
            spy.invoke(context)
        }
        verifyNoMoreCalls(spy)
    }

    @Test
    fun `process error target with reason sets messageErrorExtra and works when next is null`() = runTest {
        val action = ActionModel.from("form://update/error")
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
            actionModel = action,
            input = ProcessActionUseCase.Input.create(
                route = route,
                model = action,
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

        flow { sut.run { process(context, null) } }.collect()
        assertEquals(1, capturedInputs.size)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            useCaseExecutor.await(
                useCase = updateViewUseCase,
                input = any()
            )
        }
    }

    @Test
    fun `process error target with null jsonElement skips update and calls next`() = runTest {
        val action = ActionModel.from("form://update/error")
        val route = NavigationRoute.Url(id = "id", value = "url")

        val context = ActionMiddleware.Context(
            lockable = InteractionLockable.Empty,
            actionModel = action,
            input = ProcessActionUseCase.Input.create(
                route = route,
                model = action,
                lockable = InteractionLockable.Empty,
                jsonElement = null
            )
        )

        val spy = SpyMiddlewareNext.create<ActionMiddleware.Context>()
        val next = MockMiddlewareNext<ActionMiddleware.Context, Unit>(spy)

        flow { sut.run { process(context, next) } }.collect()

        verify(VerifyMode.exhaustiveOrder) {
            spy.invoke(context)
        }
        verifyNoMoreCalls(spy)
    }

    @Test
    fun `process throws for unknown target`() = runTest {
        val action = ActionModel.from("form://update/unknown")
        val route = NavigationRoute.Url(id = "id", value = "url")

        val context = ActionMiddleware.Context(
            lockable = InteractionLockable.Empty,
            actionModel = action,
            input = ProcessActionUseCase.Input.create(
                route = route,
                model = action,
                lockable = InteractionLockable.Empty,
                jsonElement = JsonNull
            )
        )

        assertFailsWith<DomainException> {
            flow { sut.run { process(context, null) } }.collect()
        }
    }

    @Test
    fun `process returns early when route is null and invokes next`() = runTest {
        val action = ActionModel.from("form://update/error")

        val context = ActionMiddleware.Context(
            lockable = InteractionLockable.Empty,
            actionModel = action,
            input = ProcessActionUseCase.Input.create(
                route = null,
                model = action,
                lockable = InteractionLockable.Empty,
                jsonElement = null
            )
        )

        val spy = SpyMiddlewareNext.create<ActionMiddleware.Context>()
        val next = MockMiddlewareNext<ActionMiddleware.Context, Unit>(spy)

        flow { sut.run { process(context, next) } }.collect()

        verify(VerifyMode.exhaustiveOrder) {
            spy.invoke(context)
        }
        verifyNoMoreCalls(spy)
    }
}
