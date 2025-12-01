package com.tezov.tuucho.core.domain.business.interaction.actionMiddleware

import com.tezov.tuucho.core.domain.business.di.KoinContext
import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.jsonSchema.material.action.ActionFormSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.response.FormSendSchema
import com.tezov.tuucho.core.domain.business.middleware.ActionMiddleware
import com.tezov.tuucho.core.domain.business.model.ActionModelDomain
import com.tezov.tuucho.core.domain.business.model.action.FormAction
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseExecutorProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockable
import com.tezov.tuucho.core.domain.business.protocol.screen.ScreenProtocol
import com.tezov.tuucho.core.domain.business.protocol.screen.view.form.FormViewProtocol
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.ProcessActionUseCase
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.SendDataUseCase
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.GetScreenOrNullUseCase
import com.tezov.tuucho.core.domain.tool.annotation.TuuchoInternalApi
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifyNoMoreCalls
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FormSendUrlActionMiddlewareTest {
    private lateinit var useCaseExecutor: UseCaseExecutorProtocol
    private lateinit var getScreenOrNullUseCase: GetScreenOrNullUseCase
    private lateinit var sendDataUseCase: SendDataUseCase
    private lateinit var processActionUseCase: ProcessActionUseCase

    private lateinit var sut: FormSendUrlActionMiddleware

    @BeforeTest
    fun setup() {
        useCaseExecutor = mock()
        getScreenOrNullUseCase = mock()
        sendDataUseCase = mock()
        processActionUseCase = mock()

        @OptIn(TuuchoInternalApi::class)
        KoinContext.koinApplication = koinApplication {
            modules(
                module {
                    single<ProcessActionUseCase> { processActionUseCase }
                }
            )
        }

        sut = FormSendUrlActionMiddleware(
            useCaseExecutor = useCaseExecutor,
            getScreenOrNull = getScreenOrNullUseCase,
            sendData = sendDataUseCase
        )
    }

    @AfterTest
    fun tearDown() {
        verifyNoMoreCalls(
            useCaseExecutor,
            getScreenOrNullUseCase,
            sendDataUseCase,
            processActionUseCase
        )
    }

    @Test
    fun `priority returns DEFAULT`() {
        assertEquals(ActionMiddleware.Priority.DEFAULT, sut.priority)
    }

    @Test
    fun `accept matches only form send url with non null target`() {
        val valid = ActionModelDomain.from("form://send-url/target")
        val wrongCommand = ActionModelDomain.from("x://send-url/target")
        val wrongAuthority = ActionModelDomain.from("form://other/target")
        val missingTarget = ActionModelDomain.from("form://send-url")

        assertTrue(sut.accept(null, valid))
        assertFalse(sut.accept(null, wrongCommand))
        assertFalse(sut.accept(null, wrongAuthority))
        assertFalse(sut.accept(null, missingTarget))
    }

    @Test
    fun `process returns early when route is not Url and calls next`() = runTest {
        val action = defaultAction()
        val routeBack = NavigationRoute.Back

        val context = createContext(
            route = routeBack,
            action = action,
            jsonElement = null
        )

        val next = mock<MiddlewareProtocol.Next<ActionMiddleware.Context, ProcessActionUseCase.Output>>()
        everySuspend { next.invoke(any()) } returns ProcessActionUseCase.Output.ElementArray(emptyList())

        sut.process(context, next)

        verifySuspend {
            next.invoke(context)
        }
    }

    @Test
    fun `process returns early when no screen is found and calls next`() = runTest {
        val action = defaultAction()
        val routeUrl = defaultRoute()

        val context = createContext(
            route = routeUrl,
            action = action,
            jsonElement = null
        )

        val next = mock<MiddlewareProtocol.Next<ActionMiddleware.Context, ProcessActionUseCase.Output>>()

        stubGetScreen(screen = null)

        everySuspend { next.invoke(any()) } returns ProcessActionUseCase.Output.ElementArray(emptyList())

        sut.process(context, next)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            useCaseExecutor.await(
                useCase = getScreenOrNullUseCase,
                input = GetScreenOrNullUseCase.Input(route = routeUrl)
            )
            next.invoke(context)
        }
    }

    @Test
    fun `process returns early when form view list is null and next is null`() = runTest {
        val action = defaultAction()
        val routeUrl = defaultRoute()

        stubGetScreen(screen = null)

        val context = createContext(
            route = routeUrl,
            action = action,
            jsonElement = null
        )

        sut.process(context, null)

        verifySuspend {
            useCaseExecutor.await(
                useCase = getScreenOrNullUseCase,
                input = GetScreenOrNullUseCase.Input(route = routeUrl)
            )
        }
    }

    @Test
    fun `process runs invalid local form flow when at least one field is invalid`() = runTest {
        // ---------- action, route, and context setup ----------
        val action = defaultAction()
        val routeUrl = defaultRoute()

        val formViewInvalid = mock<FormViewProtocol>()
        val formViewValid = mock<FormViewProtocol>()

        // ---------- map form views from extensions ----------
        val screen = mockScreenWithFormViews(formViewInvalid, formViewValid)

        // ---------- validity results ----------
        stubInvalidFormView(
            formView = formViewInvalid,
            id = "field-invalid"
        )
        stubValidFormView(
            formView = formViewValid,
            id = "field-valid",
            value = "valid"
        )

        // ---------- use case stubbing ----------
        stubGetScreen(screen = screen)

        stubProcessActionAny()

        // ---------- next middleware ----------
        val next = mock<MiddlewareProtocol.Next<ActionMiddleware.Context, ProcessActionUseCase.Output>>()
        everySuspend { next.invoke(any()) } returns ProcessActionUseCase.Output.ElementArray(emptyList())

        // ---------- middleware context ----------
        val context = createContext(
            route = routeUrl,
            action = action,
            jsonElement = null
        )

        // ---------- Test ----------
        sut.process(context, next)

        // ---------- Verify ----------
        verifySuspend(VerifyMode.exhaustiveOrder) {
            useCaseExecutor.await(
                useCase = getScreenOrNullUseCase,
                input = GetScreenOrNullUseCase.Input(route = routeUrl)
            )
            useCaseExecutor.await(
                useCase = processActionUseCase,
                input = any()
            )
            next.invoke(context)
        }
    }

    @Test
    fun `process runs valid local form and sendData returns null`() = runTest {
        // ---------- action, route, and context setup ----------
        val action = defaultAction()
        val routeUrl = defaultRoute()

        val formViewValid = mock<FormViewProtocol>()

        // ---------- map form views from extensions ----------
        val screen = mockScreenWithFormViews(formViewValid)

        // ---------- validity results ----------
        stubValidFormView(formViewValid)

        // ---------- use case stubbing ----------
        stubGetScreen(screen = screen)

        everySuspend {
            useCaseExecutor.await(
                useCase = sendDataUseCase,
                input = any()
            )
        } returns null

        // ---------- next middleware ----------
        val next = mock<MiddlewareProtocol.Next<ActionMiddleware.Context, ProcessActionUseCase.Output>>()
        everySuspend { next.invoke(any()) } returns ProcessActionUseCase.Output.ElementArray(emptyList())

        // ---------- middleware context ----------
        val context = createContext(
            route = routeUrl,
            action = action,
            jsonElement = null
        )

        // ---------- Test ----------
        sut.process(context, next)

        // ---------- Verify ----------
        verifySuspend(VerifyMode.exhaustiveOrder) {
            useCaseExecutor.await(
                useCase = getScreenOrNullUseCase,
                input = GetScreenOrNullUseCase.Input(route = routeUrl)
            )
            useCaseExecutor.await(
                useCase = sendDataUseCase,
                input = any()
            )
            next.invoke(context)
        }
    }

    @Test
    fun `process ignores response when type is not form`() = runTest {
        // ---------- action, route, and context setup ----------
        val action = defaultAction()
        val routeUrl = defaultRoute()

        val formViewValid = mock<FormViewProtocol>()

        // ---------- map form views from extensions ----------
        val screen = mockScreenWithFormViews(formViewValid)

        // ---------- validity results ----------
        stubValidFormView(formViewValid)

        // ---------- use case stubbing ----------
        stubGetScreen(screen = screen)

        val responseObject = buildJsonObject {
            put(FormSendSchema.Key.type, JsonPrimitive("other"))
        }

        everySuspend {
            useCaseExecutor.await(
                useCase = sendDataUseCase,
                input = any()
            )
        } returns SendDataUseCase.Output(jsonObject = responseObject)

        // ---------- next middleware ----------
        val next = mock<MiddlewareProtocol.Next<ActionMiddleware.Context, ProcessActionUseCase.Output>>()
        everySuspend { next.invoke(any()) } returns ProcessActionUseCase.Output.ElementArray(emptyList())

        // ---------- middleware context ----------
        val context = createContext(
            route = routeUrl,
            action = action,
            jsonElement = null
        )

        // ---------- Test ----------
        sut.process(context, next)

        // ---------- Verify ----------
        verifySuspend(VerifyMode.exhaustiveOrder) {
            useCaseExecutor.await(
                useCase = getScreenOrNullUseCase,
                input = GetScreenOrNullUseCase.Input(route = routeUrl)
            )
            useCaseExecutor.await(
                useCase = sendDataUseCase,
                input = any()
            )
            next.invoke(context)
        }
    }

    @Test
    fun `process runs valid remote form flow with before validated after actions`() = runTest {
        // ---------- action, route, and context setup ----------
        val action = defaultAction()
        val routeUrl = defaultRoute()

        val formViewValid = mock<FormViewProtocol>()

        // ---------- map form views from extensions ----------
        val screen = mockScreenWithFormViews(formViewValid)

        // ---------- validity ----------
        stubValidFormView(formViewValid)

        // ---------- GetScreenOrNull stubbing ----------
        stubGetScreen(screen = screen)

        // ---------- remote response with before/validated/after ----------
        val responseObject = buildJsonObject {
            put(FormSendSchema.Key.subset, JsonPrimitive(FormSendSchema.Value.subset))
            put(FormSendSchema.Key.allSucceed, JsonPrimitive(true))
            put(
                FormSendSchema.Key.action,
                buildJsonObject {
                    put(
                        FormSendSchema.Action.Key.before,
                        buildJsonArray { add(JsonPrimitive("cmd://before")) }
                    )
                    put(
                        FormSendSchema.Action.Key.after,
                        buildJsonArray { add(JsonPrimitive("cmd://after")) }
                    )
                }
            )
        }

        // ---------- SendData stubbing ----------
        everySuspend {
            useCaseExecutor.await(
                useCase = sendDataUseCase,
                input = any()
            )
        } returns SendDataUseCase.Output(jsonObject = responseObject)

        // ---------- ProcessAction stubbing (before, validated, after) ----------
        stubProcessActionAny()

        // ---------- validated actions in jsonElement ----------
        val jsonElement = buildJsonObject {
            put(
                ActionFormSchema.Send.Key.validated,
                buildJsonArray {
                    add(JsonPrimitive("cmd://validated"))
                }
            )
        }

        // ---------- next middleware ----------
        val next = mock<MiddlewareProtocol.Next<ActionMiddleware.Context, ProcessActionUseCase.Output>>()
        everySuspend { next.invoke(any()) } returns ProcessActionUseCase.Output.ElementArray(emptyList())

        // ---------- middleware context ----------
        val context = createContext(
            route = routeUrl,
            action = action,
            jsonElement = jsonElement
        )

        // ---------- Test ----------
        sut.process(context, next)

        // ---------- Verify ----------
        verifySuspend(VerifyMode.exhaustiveOrder) {
            // 1. Retrieve form views
            useCaseExecutor.await(
                useCase = getScreenOrNullUseCase,
                input = GetScreenOrNullUseCase.Input(route = routeUrl)
            )

            // 2. Send data to remote
            useCaseExecutor.await(
                useCase = sendDataUseCase,
                input = any()
            )

            // 3. BEFORE action
            useCaseExecutor.await(
                useCase = processActionUseCase,
                input = any()
            )

            // 4. VALIDATED action
            useCaseExecutor.await(
                useCase = processActionUseCase,
                input = any()
            )

            // 5. AFTER action
            useCaseExecutor.await(
                useCase = processActionUseCase,
                input = any()
            )

            // 6. next middleware
            next.invoke(context)
        }
    }

    @Test
    fun `process runs invalid remote form flow with before failure denied after actions`() = runTest {
        // ---------- action, route, and context setup ----------
        val action = defaultAction()
        val routeUrl = defaultRoute()

        val formViewValid = mock<FormViewProtocol>()

        // ---------- map form views from extensions ----------
        val screen = mockScreenWithFormViews(formViewValid)

        stubValidFormView(formViewValid)

        // ---------- use case stubbing: GetScreenOrNull ----------
        stubGetScreen(screen = screen)

        // ---------- remote failure response ----------
        val failureResultArray = buildJsonArray {
            add(
                buildJsonObject {
                    put(FormSendSchema.FailureResult.Key.id, JsonPrimitive("field-failure"))
                    put(
                        FormSendSchema.FailureResult.Key.reason,
                        buildJsonObject { put("code", JsonPrimitive("error-code")) }
                    )
                }
            )
        }

        val responseObject = buildJsonObject {
            put(FormSendSchema.Key.subset, JsonPrimitive(FormSendSchema.Value.subset))
            put(FormSendSchema.Key.allSucceed, JsonPrimitive(false))
            put(FormSendSchema.Key.failureResult, failureResultArray)
            put(
                FormSendSchema.Key.action,
                buildJsonObject {
                    put(
                        FormSendSchema.Action.Key.before,
                        buildJsonArray {
                            add(JsonPrimitive("cmd://before"))
                        }
                    )
                    put(
                        FormSendSchema.Action.Key.after,
                        buildJsonArray {
                            add(JsonPrimitive("cmd://after"))
                        }
                    )
                }
            )
        }

        // ---------- use case stubbing: SendData ----------
        everySuspend {
            useCaseExecutor.await(
                useCase = sendDataUseCase,
                input = any()
            )
        } returns SendDataUseCase.Output(jsonObject = responseObject)

        // ---------- use case stubbing: ProcessAction (before, error, denied, after) ----------
        stubProcessActionAny()

        // ---------- jsonElement with denied actions (correct shape) ----------
        val jsonElement = buildJsonObject {
            put(
                ActionFormSchema.Send.Key.denied,
                buildJsonArray {
                    add(JsonPrimitive("cmd://denied"))
                }
            )
        }

        // ---------- next middleware ----------
        val next = mock<MiddlewareProtocol.Next<ActionMiddleware.Context, ProcessActionUseCase.Output>>()
        everySuspend { next.invoke(any()) } returns ProcessActionUseCase.Output.ElementArray(emptyList())

        // ---------- middleware context ----------
        val lockable = InteractionLockable.Empty

        val context = ActionMiddleware.Context(
            lockable = lockable,
            input = ProcessActionUseCase.Input.JsonElement(
                route = routeUrl,
                action = action,
                lockable = lockable,
                jsonElement = jsonElement
            )
        )

        // ---------- Test ----------
        sut.process(context, next)

        // ---------- Verify ----------
        verifySuspend(VerifyMode.exhaustiveOrder) {
            useCaseExecutor.await(
                useCase = getScreenOrNullUseCase,
                input = GetScreenOrNullUseCase.Input(route = routeUrl)
            )
            useCaseExecutor.await(
                useCase = sendDataUseCase,
                input = any()
            )
            useCaseExecutor.await(
                useCase = processActionUseCase,
                input = any() // before
            )
            useCaseExecutor.await(
                useCase = processActionUseCase,
                input = any() // dispatchActionCommandError
            )
            useCaseExecutor.await(
                useCase = processActionUseCase,
                input = any() // denied
            )
            useCaseExecutor.await(
                useCase = processActionUseCase,
                input = any() // after
            )
            next.invoke(context)
        }
    }

    @Test
    fun `process throws when action target is null while sending data`() = runTest {
        // ---------- action with null target ----------
        val action = ActionModelDomain.from(
            command = FormAction.Send.command,
            authority = FormAction.Send.authority,
            target = null,
            query = null as JsonElement?
        )
        val routeUrl = defaultRoute()

        // ---------- form and screen setup ----------
        val formViewValid = mock<FormViewProtocol>()
        val screen = mockScreenWithFormViews(formViewValid)

        // ---------- valid form so we actually reach the sendData branch ----------
        stubValidFormView(formViewValid)

        // ---------- use case stubbing: GetScreenOrNull only ----------
        stubGetScreen(screen = screen)

        // ---------- next middleware (never reached after exception) ----------
        val next = mock<MiddlewareProtocol.Next<ActionMiddleware.Context, ProcessActionUseCase.Output>>()

        // ---------- middleware context ----------
        val context = createContext(
            route = routeUrl,
            action = action,
            jsonElement = null
        )

        // ---------- Test: expect DomainException from null target in SendData input ----------
        assertFailsWith<DomainException> {
            sut.process(context, next)
        }

        // ---------- Verify the single await call so tearDown's verifyNoMoreCalls passes ----------
        verifySuspend {
            useCaseExecutor.await(
                useCase = getScreenOrNullUseCase,
                input = GetScreenOrNullUseCase.Input(route = routeUrl)
            )
        }
    }

    // -------------------------------------------------------
    // Helpers
    // -------------------------------------------------------

    private fun defaultAction() = ActionModelDomain.from("form://send-url/target")

    private fun defaultRoute() = NavigationRoute.Url(id = "id", value = "url")

    private fun createContext(
        route: NavigationRoute,
        action: ActionModelDomain,
        jsonElement: JsonElement?,
        lockable: InteractionLockable = InteractionLockable.Empty
    ) = ActionMiddleware.Context(
        lockable = lockable,
        input = ProcessActionUseCase.Input.JsonElement(
            route = route,
            action = action,
            lockable = lockable,
            jsonElement = jsonElement
        )
    )

    private fun mockScreenWithFormViews(
        vararg formViews: FormViewProtocol
    ): ScreenProtocol {
        val screen = mock<ScreenProtocol>()
        val extensions = formViews.map { formView ->
            val ext = mock<FormViewProtocol.Extension<FormViewProtocol>>()
            every { ext.formView } returns formView
            ext
        }
        every { screen.views(FormViewProtocol.Extension::class) } returns extensions
        return screen
    }

    private fun stubValidFormView(
        formView: FormViewProtocol,
        id: String = "field",
        value: String = "value"
    ) {
        every { formView.updateValidity() } returns Unit
        every { formView.isValid() } returns true
        every { formView.getId() } returns id
        every { formView.getValue() } returns value
    }

    private fun stubInvalidFormView(
        formView: FormViewProtocol,
        id: String
    ) {
        every { formView.updateValidity() } returns Unit
        every { formView.isValid() } returns false
        every { formView.getId() } returns id
    }

    private fun stubGetScreen(
        screen: ScreenProtocol?
    ) {
        everySuspend {
            useCaseExecutor.await(
                useCase = getScreenOrNullUseCase,
                input = any()
            )
        } returns GetScreenOrNullUseCase.Output(screen = screen)
    }

    private fun stubProcessActionAny() {
        everySuspend {
            useCaseExecutor.await(
                useCase = processActionUseCase,
                input = any()
            )
        } returns ProcessActionUseCase.Output.ElementArray(emptyList())
    }
}
