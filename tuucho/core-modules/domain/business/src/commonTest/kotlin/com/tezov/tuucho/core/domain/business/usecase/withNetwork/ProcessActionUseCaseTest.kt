//package com.tezov.tuucho.core.domain.business.usecase.withNetwork
//
//import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
//import com.tezov.tuucho.core.domain.business.model.action.ActionModel
//import com.tezov.tuucho.core.domain.business.protocol.ActionExecutorProtocol
//import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockable
//import dev.mokkery.answering.returns
//import dev.mokkery.everySuspend
//import dev.mokkery.matcher.any
//import dev.mokkery.mock
//import dev.mokkery.verify.VerifyMode
//import dev.mokkery.verifyNoMoreCalls
//import dev.mokkery.verifySuspend
//import kotlinx.coroutines.test.runTest
//import kotlinx.serialization.json.JsonNull
//import kotlinx.serialization.json.JsonObject
//import kotlin.test.AfterTest
//import kotlin.test.BeforeTest
//import kotlin.test.Test
//import kotlin.test.assertSame
//
//class ProcessActionUseCaseTest {
//    private lateinit var actionExecutor: ActionExecutorProtocol
//    private lateinit var sut: ProcessActionUseCase
//
//    @BeforeTest
//    fun setup() {
//        actionExecutor = mock()
//        sut = ProcessActionUseCase(
//            actionExecutor = actionExecutor
//        )
//    }
//
//    @AfterTest
//    fun tearDown() {
//        verifyNoMoreCalls(actionExecutor)
//    }
//
//    @Test
//    fun `invoke executes executor through useCase scope for Action input`() = runTest {
//        val routeBack = NavigationRoute.Back
//        val lockableEmpty = InteractionLockable.Empty
//        val actionModel = ActionModel.from("cmd://auth/target")
//
//        val input = ProcessActionUseCase.Input.ActionModel(
//            route = routeBack,
//            actionModel = actionModel,
//            lockable = lockableEmpty,
//            jsonElement = JsonNull
//        )
//
//        val elementOutput = ProcessActionUseCase.Output.Element(
//            type = String::class,
//            rawValue = "value"
//        )
//
//        val expectedOutput = ProcessActionUseCase.Output.ElementArray(
//            listOf(elementOutput)
//        )
//
//        everySuspend { actionExecutor.process(any()) } returns expectedOutput
//
//        val result = sut.invoke(input)
//
//        assertSame(expectedOutput, result)
//
//        verifySuspend(VerifyMode.exhaustiveOrder) {
//            actionExecutor.process(input = input)
//        }
//    }
//
//    @Test
//    fun `invoke executes executor through useCase scope for ActionObject input`() = runTest {
//        val routeUrl = NavigationRoute.Url(id = "id", value = "url")
//        val lockableEmpty = InteractionLockable.Empty
//
//        val input = ProcessActionUseCase.Input.ActionObject(
//            route = routeUrl,
//            actionObject = JsonObject(emptyMap()),
//            lockable = lockableEmpty
//        )
//
//        val expectedOutput = ProcessActionUseCase.Output.Element(
//            type = Int::class,
//            rawValue = 3
//        )
//
//        everySuspend { actionExecutor.process(any()) } returns expectedOutput
//
//        val result = sut.invoke(input)
//
//        assertSame(expectedOutput, result)
//
//        verifySuspend(VerifyMode.exhaustiveOrder) {
//            actionExecutor.process(input = input)
//        }
//    }
//}
