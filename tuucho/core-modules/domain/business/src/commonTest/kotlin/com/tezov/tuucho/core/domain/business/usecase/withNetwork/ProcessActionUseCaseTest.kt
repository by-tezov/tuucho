package com.tezov.tuucho.core.domain.business.usecase.withNetwork

import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.model.action.ActionModel
import com.tezov.tuucho.core.domain.business.protocol.ActionExecutorProtocol
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifyNoMoreCalls
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ProcessActionUseCaseTest {
    private lateinit var actionExecutor: ActionExecutorProtocol
    private lateinit var sut: ProcessActionUseCase

    @BeforeTest
    fun setup() {
        actionExecutor = mock()
        sut = ProcessActionUseCase(
            actionExecutor = actionExecutor
        )
    }

    @AfterTest
    fun tearDown() {
        verifyNoMoreCalls(actionExecutor)
    }

    @Test
    fun `invoke executes executor with actionModel`() = runTest {
        val routeBack = NavigationRoute.Back
        val actionModel = ActionModel.from("cmd://auth/target")

        val input = ProcessActionUseCase.Input.create(
            route = routeBack,
            models = listOf(actionModel)
        )

        everySuspend { actionExecutor.process(any()) } returns Unit
        sut.invoke(input)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            actionExecutor.process(input = input)
        }
    }

    @Test
    fun `invoke executes executor with modelObject`() = runTest {
        val routeBack = NavigationRoute.Finish

        val input = ProcessActionUseCase.Input.create(
            route = routeBack,
            modelObject = buildJsonObject {
                put("primaries", buildJsonArray {
                    add("cmd://auth/target")
                })
            }
        )
        assertEquals(listOf("cmd://auth/target"), input.models.map { it.toString() })

        everySuspend { actionExecutor.process(any()) } returns Unit
        sut.invoke(input)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            actionExecutor.process(input = input)
        }
    }
}
