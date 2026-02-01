package com.tezov.tuucho.core.domain.business.interaction.actionMiddleware

import com.tezov.tuucho.core.domain.business._system.koin.KoinIsolatedContext
import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.jsonSchema.material.action.ActionFormSchema
import com.tezov.tuucho.core.domain.business.jsonSchema.response.FormSendSchema
import com.tezov.tuucho.core.domain.business.mock.MockMiddlewareNext
import com.tezov.tuucho.core.domain.business.mock.SpyMiddlewareNext
import com.tezov.tuucho.core.domain.business.model.action.ActionModel
import com.tezov.tuucho.core.domain.business.model.action.FormActionDefinition
import com.tezov.tuucho.core.domain.business.protocol.ActionMiddlewareProtocol
import com.tezov.tuucho.core.domain.business.protocol.UseCaseExecutorProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockable
import com.tezov.tuucho.core.domain.business.protocol.screen.ScreenProtocol
import com.tezov.tuucho.core.domain.business.protocol.screen.view.FormStateProtocol
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.ProcessActionUseCase
import com.tezov.tuucho.core.domain.business.usecase.withNetwork.SendDataUseCase
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.GetScreenOrNullUseCase
import com.tezov.tuucho.core.domain.tool.annotation.TuuchoInternalApi
import dev.mokkery.answering.returns
import dev.mokkery.every
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
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
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
        KoinIsolatedContext.koinApplication = koinApplication {
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
        assertEquals(ActionMiddlewareProtocol.Priority.DEFAULT, sut.priority)
    }

    @Test
    fun `accept matches only form send url with non null target`() {
        val valid = ActionModel.from("form://send-url/target")
        val wrongCommand = ActionModel.from("x://send-url/target")
        val wrongAuthority = ActionModel.from("form://other/target")
        val missingTarget = ActionModel.from("form://send-url")

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
            action = action
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
    fun `process returns early when no screen is found and calls next`() = runTest {
        val action = defaultAction()
        val routeUrl = defaultRoute()

        val context = createContext(
            route = routeUrl,
            action = action
        )

        mockGetScreen(screen = null)
        val spy = SpyMiddlewareNext.create<ActionMiddlewareProtocol.Context>()
        val next = MockMiddlewareNext<ActionMiddlewareProtocol.Context, Unit>(spy)

        flow { sut.run { process(context, next) } }.collect()

        verifySuspend(VerifyMode.exhaustiveOrder) {
            useCaseExecutor.await(
                useCase = getScreenOrNullUseCase,
                input = GetScreenOrNullUseCase.Input(route = routeUrl)
            )
            spy.invoke(context)
        }
        verifyNoMoreCalls(spy)
    }

    @Test
    fun `process returns early when form view list is null and next is null`() = runTest {
        val action = defaultAction()
        val routeUrl = defaultRoute()

        mockGetScreen(screen = null)

        val context = createContext(
            route = routeUrl,
            action = action
        )

        flow { sut.run { process(context, null) } }.collect()

        verifySuspend(VerifyMode.exhaustiveOrder) {
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

        val formViewInvalid = mock<FormStateProtocol>()
        val formViewValid = mock<FormStateProtocol>()

        // ---------- map form views from extensions ----------
        val screen = mockScreenWithFormViews(formViewInvalid, formViewValid)

        // ---------- validity results ----------
        mockInvalidFormView(
            formView = formViewInvalid,
            id = "field-invalid"
        )
        mockValidFormView(
            formView = formViewValid,
            id = "field-valid",
            value = "valid"
        )

        // ---------- use case stubbing ----------
        mockGetScreen(screen = screen)

        mockProcessActionAny()

        // ---------- next middleware ----------
        val spy = SpyMiddlewareNext.create<ActionMiddlewareProtocol.Context>()
        val next = MockMiddlewareNext<ActionMiddlewareProtocol.Context, Unit>(spy)

        // ---------- middleware context ----------
        val context = createContext(
            route = routeUrl,
            action = action
        )

        // ---------- Test ----------
        flow { sut.run { process(context, next) } }.collect()

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
            spy.invoke(context)
        }
        verifyNoMoreCalls(spy)
    }

    @Test
    fun `process runs valid local form and sendData returns null`() = runTest {
        // ---------- action, route, and context setup ----------
        val action = defaultAction()
        val routeUrl = defaultRoute()

        val formViewValid = mock<FormStateProtocol>()

        // ---------- map form views from extensions ----------
        val screen = mockScreenWithFormViews(formViewValid)

        // ---------- validity results ----------
        mockValidFormView(formViewValid)

        // ---------- use case stubbing ----------
        mockGetScreen(screen = screen)

        everySuspend {
            useCaseExecutor.await(
                useCase = sendDataUseCase,
                input = any()
            )
        } returns null

        // ---------- next middleware ----------
        val spy = SpyMiddlewareNext.create<ActionMiddlewareProtocol.Context>()
        val next = MockMiddlewareNext<ActionMiddlewareProtocol.Context, Unit>(spy)

        // ---------- middleware context ----------
        val context = createContext(
            route = routeUrl,
            action = action
        )

        // ---------- Test ----------
        flow { sut.run { process(context, next) } }.collect()

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
            spy.invoke(context)
        }
        verifyNoMoreCalls(spy)
    }

    @Test
    fun `process ignores response when type is not form`() = runTest {
        // ---------- action, route, and context setup ----------
        val action = defaultAction()
        val routeUrl = defaultRoute()

        val formViewValid = mock<FormStateProtocol>()

        // ---------- map form views from extensions ----------
        val screen = mockScreenWithFormViews(formViewValid)

        // ---------- validity results ----------
        mockValidFormView(formViewValid)

        // ---------- use case stubbing ----------
        mockGetScreen(screen = screen)

        val responseObject = buildJsonObject {
            put(FormSendSchema.Key.type, "other")
        }

        everySuspend {
            useCaseExecutor.await(
                useCase = sendDataUseCase,
                input = any()
            )
        } returns SendDataUseCase.Output(jsonObject = responseObject)

        // ---------- next middleware ----------
        val spy = SpyMiddlewareNext.create<ActionMiddlewareProtocol.Context>()
        val next = MockMiddlewareNext<ActionMiddlewareProtocol.Context, Unit>(spy)

        // ---------- middleware context ----------
        val context = createContext(
            route = routeUrl,
            action = action
        )

        // ---------- Test ----------
        flow { sut.run { process(context, next) } }.collect()

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
            spy.invoke(context)
        }
        verifyNoMoreCalls(spy)
    }

    @Test
    fun `process runs valid remote form flow with before validated after actions`() = runTest {
        // ---------- action, route, and context setup ----------
        val action = defaultAction()
        val routeUrl = defaultRoute()

        val formViewValid = mock<FormStateProtocol>()

        // ---------- map form views from extensions ----------
        val screen = mockScreenWithFormViews(formViewValid)

        // ---------- validity ----------
        mockValidFormView(formViewValid)

        // ---------- GetScreenOrNull stubbing ----------
        mockGetScreen(screen = screen)

        // ---------- remote response with before/validated/after ----------
        val responseObject = buildJsonObject {
            put(FormSendSchema.Key.subset, FormSendSchema.Value.subset)
            put(FormSendSchema.Key.allSucceed, true)
            put(
                FormSendSchema.Key.action,
                buildJsonObject {
                    put(
                        FormSendSchema.Action.Key.before,
                        buildJsonArray { add("cmd://before") }
                    )
                    put(
                        FormSendSchema.Action.Key.after,
                        buildJsonArray { add("cmd://after") }
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
        mockProcessActionAny()

        // ---------- validated actions in jsonElement ----------
        val jsonElement = buildJsonObject {
            put(
                ActionFormSchema.Send.Key.validated,
                buildJsonArray {
                    add("cmd://validated")
                }
            )
        }

        // ---------- next middleware ----------
        val spy = SpyMiddlewareNext.create<ActionMiddlewareProtocol.Context>()
        val next = MockMiddlewareNext<ActionMiddlewareProtocol.Context, Unit>(spy)

        // ---------- middleware context ----------
        val context = createContext(
            route = routeUrl,
            action = action,
            actionObjectOriginal = jsonElement
        )

        // ---------- Test ----------
        flow { sut.run { process(context, next) } }.collect()

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
            spy.invoke(context)
        }
        verifyNoMoreCalls(spy)
    }

    @Test
    fun `process runs invalid remote form flow with before failure denied after actions`() = runTest {
        // ---------- action, route, and context setup ----------
        val action = defaultAction()
        val routeUrl = defaultRoute()

        val formViewValid = mock<FormStateProtocol>()

        // ---------- map form views from extensions ----------
        val screen = mockScreenWithFormViews(formViewValid)

        mockValidFormView(formViewValid)

        // ---------- use case stubbing: GetScreenOrNull ----------
        mockGetScreen(screen = screen)

        // ---------- remote failure response ----------
        val failureResultArray = buildJsonArray {
            add(
                buildJsonObject {
                    put(FormSendSchema.FailureResult.Key.id, "field-failure")
                    put(
                        FormSendSchema.FailureResult.Key.reason,
                        buildJsonObject { put("code", "error-code") }
                    )
                }
            )
        }

        val responseObject = buildJsonObject {
            put(FormSendSchema.Key.subset, FormSendSchema.Value.subset)
            put(FormSendSchema.Key.allSucceed, false)
            put(FormSendSchema.Key.failureResults, failureResultArray)
            put(
                FormSendSchema.Key.action,
                buildJsonObject {
                    put(
                        FormSendSchema.Action.Key.before,
                        buildJsonArray {
                            add("cmd://before")
                        }
                    )
                    put(
                        FormSendSchema.Action.Key.after,
                        buildJsonArray {
                            add("cmd://after")
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
        mockProcessActionAny()

        // ---------- jsonElement with denied actions (correct shape) ----------
        val modelObjectOriginal = buildJsonObject {
            put(
                ActionFormSchema.Send.Key.denied,
                buildJsonArray {
                    add("cmd://denied")
                }
            )
        }

        // ---------- next middleware ----------
        val spy = SpyMiddlewareNext.create<ActionMiddlewareProtocol.Context>()
        val next = MockMiddlewareNext<ActionMiddlewareProtocol.Context, Unit>(spy)

        // ---------- middleware context ----------
        val lockable = InteractionLockable.Empty

        val context = ActionMiddlewareProtocol.Context(
            lockable = lockable,
            actionModel = action,
            input = ProcessActionUseCase.Input(
                route = routeUrl,
                models = listOf(action),
                modelObjectOriginal = modelObjectOriginal,
                lockable = lockable,
                jsonElement = null
            )
        )

        // ---------- Test ----------
        flow { sut.run { process(context, next) } }.collect()

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
            spy.invoke(context)
        }
        verifyNoMoreCalls(spy)
    }

    @Test
    fun `process throws when action target is null while sending data`() = runTest {
        // ---------- action with null target ----------
        val action = ActionModel.from(
            command = FormActionDefinition.Send.command,
            authority = FormActionDefinition.Send.authority,
            target = null,
            query = null
        )
        val routeUrl = defaultRoute()

        // ---------- form and screen setup ----------
        val formViewValid = mock<FormStateProtocol>()
        val screen = mockScreenWithFormViews(formViewValid)

        // ---------- valid form so we actually reach the sendData branch ----------
        mockValidFormView(formViewValid)

        // ---------- use case stubbing: GetScreenOrNull only ----------
        mockGetScreen(screen = screen)

        // ---------- middleware context ----------
        val context = createContext(
            route = routeUrl,
            action = action
        )

        // ---------- Test: expect DomainException from null target in SendData input ----------
        assertFailsWith<DomainException> {
            flow { sut.run { process(context, null) } }.collect()
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

    private fun defaultAction() = ActionModel.from("form://send-url/target")

    private fun defaultRoute() = NavigationRoute.Url(id = "id", value = "url")

    private fun createContext(
        route: NavigationRoute,
        action: ActionModel,
        actionObjectOriginal: JsonObject? = null,
        jsonElement: JsonElement? = null,
        lockable: InteractionLockable = InteractionLockable.Empty
    ) = ActionMiddlewareProtocol.Context(
        lockable = lockable,
        actionModel = action,
        input = ProcessActionUseCase.Input(
            route = route,
            models = listOf(action),
            lockable = lockable,
            modelObjectOriginal = actionObjectOriginal,
            jsonElement = jsonElement
        )
    )

    private fun mockScreenWithFormViews(
        vararg formViews: FormStateProtocol
    ): ScreenProtocol {
        val screen = mock<ScreenProtocol>()
        val extensions = formViews.map { formView ->
            val ext = mock<FormStateProtocol.Extension>()
            every { ext.extensionFormState } returns formView
            ext
        }
        everySuspend { screen.views(FormStateProtocol.Extension::class) } returns extensions
        return screen
    }

    private fun mockValidFormView(
        formView: FormStateProtocol,
        id: String = "field",
        value: String = "value"
    ) {
        every { formView.updateValidity() } returns Unit
        every { formView.isValid() } returns true
        every { formView.getId() } returns id
        every { formView.getValue() } returns value
    }

    private fun mockInvalidFormView(
        formView: FormStateProtocol,
        id: String
    ) {
        every { formView.updateValidity() } returns Unit
        every { formView.isValid() } returns false
        every { formView.getId() } returns id
    }

    private fun mockGetScreen(
        screen: ScreenProtocol?
    ) {
        everySuspend {
            useCaseExecutor.await(
                useCase = getScreenOrNullUseCase,
                input = any()
            )
        } returns GetScreenOrNullUseCase.Output(screen = screen)
    }

    private fun mockProcessActionAny() {
        everySuspend {
            useCaseExecutor.await(
                useCase = processActionUseCase,
                input = any()
            )
        } returns Unit
    }
}
