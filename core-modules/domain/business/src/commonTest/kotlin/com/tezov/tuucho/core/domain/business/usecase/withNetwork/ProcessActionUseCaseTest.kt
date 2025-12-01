package com.tezov.tuucho.core.domain.business.usecase.withNetwork

import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.mock.CoroutineTestScope
import com.tezov.tuucho.core.domain.business.model.ActionModelDomain
import com.tezov.tuucho.core.domain.business.protocol.ActionExecutorProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.InteractionLockable
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifyNoMoreCalls
import dev.mokkery.verifySuspend
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertSame

class ProcessActionUseCaseTest {
    private val coroutineTestScope = CoroutineTestScope()

    private lateinit var actionExecutor: ActionExecutorProtocol
    private lateinit var sut: ProcessActionUseCase

    @BeforeTest
    fun setup() {
        coroutineTestScope.setup()
        actionExecutor = mock()
        sut = ProcessActionUseCase(
            coroutineScopes = coroutineTestScope.mock,
            actionExecutor = actionExecutor
        )
    }

    @AfterTest
    fun tearDown() {
        coroutineTestScope.verifyNoMoreCalls()
        verifyNoMoreCalls(actionExecutor)
    }

    @Test
    fun `invoke executes executor through useCase scope for JsonElement input`() = coroutineTestScope.run {
        val routeBack = NavigationRoute.Back
        val lockableEmpty = InteractionLockable.Empty
        val actionModel = ActionModelDomain.from("cmd://auth/target")
        val jsonElementValue = JsonNull

        val input = ProcessActionUseCase.Input.JsonElement(
            route = routeBack,
            action = actionModel,
            lockable = lockableEmpty,
            jsonElement = jsonElementValue
        )

        val elementOutput = ProcessActionUseCase.Output.Element(
            type = String::class,
            rawValue = "value"
        )

        val expectedOutput = ProcessActionUseCase.Output.ElementArray(
            listOf(elementOutput)
        )

        everySuspend { actionExecutor.process(any()) } returns expectedOutput

        val result = sut.invoke(input)

        assertSame(expectedOutput, result)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineTestScope.mock.useCase.await<Any>(any())
            actionExecutor.process(input = input)
        }
    }

    @Test
    fun `invoke executes executor through useCase scope for ActionObject input`() = coroutineTestScope.run {
        val routeUrl = NavigationRoute.Url(id = "id", value = "url")
        val lockableEmpty = InteractionLockable.Empty
        val jsonObjectValue = JsonObject(emptyMap())

        val input = ProcessActionUseCase.Input.ActionObject(
            route = routeUrl,
            actionObject = jsonObjectValue,
            lockable = lockableEmpty
        )

        val expectedOutput = ProcessActionUseCase.Output.Element(
            type = Int::class,
            rawValue = 3
        )

        everySuspend { actionExecutor.process(any()) } returns expectedOutput

        val result = sut.invoke(input)

        assertSame(expectedOutput, result)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineTestScope.mock.useCase.await<Any>(any())
            actionExecutor.process(input = input)
        }
    }
}
