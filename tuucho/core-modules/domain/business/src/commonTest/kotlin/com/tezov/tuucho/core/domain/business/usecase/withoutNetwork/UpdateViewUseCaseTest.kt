package com.tezov.tuucho.core.domain.business.usecase.withoutNetwork

import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.middleware.UpdateViewMiddleware
import com.tezov.tuucho.core.domain.business.mock.middleware.MockMiddlewareExecutor
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareExecutorProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.NavigationRepositoryProtocol
import com.tezov.tuucho.core.domain.business.protocol.screen.ScreenProtocol
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifyNoMoreCalls
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.JsonObject
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class UpdateViewUseCaseTest {
    private lateinit var middlewareExecutor: MiddlewareExecutorProtocol

    private lateinit var navigationScreenStackRepository: NavigationRepositoryProtocol.StackScreen
    private lateinit var updateViewMiddlewares: List<UpdateViewMiddleware>

    private lateinit var sut: UpdateViewUseCase

    @BeforeTest
    fun setup() {
        middlewareExecutor = MockMiddlewareExecutor()
        navigationScreenStackRepository = mock()
        updateViewMiddlewares = listOf()
        sut = UpdateViewUseCase(
            navigationScreenStackRepository = navigationScreenStackRepository,
            middlewareExecutor = middlewareExecutor,
            updateViewMiddlewares = updateViewMiddlewares
        )
    }

    @AfterTest
    fun tearDown() {
        verifyNoMoreCalls(
            navigationScreenStackRepository,
        )
    }

    @Test
    fun `invoke updates view when view exists`() = runTest {
        val routeValue = NavigationRoute.Back
        val jsonObjects = listOf(JsonObject(emptyMap()))

        val input = UpdateViewUseCase.Input(
            route = routeValue,
            jsonObjects = jsonObjects
        )

        val screen = mock<ScreenProtocol>()

        everySuspend { navigationScreenStackRepository.getScreenOrNull(routeValue) } returns screen
        everySuspend { screen.update(any<List<JsonObject>>()) } returns Unit

        sut.invoke(input)
        verifySuspend(VerifyMode.exhaustiveOrder) {
            navigationScreenStackRepository.getScreenOrNull(routeValue)
            screen.update(jsonObjects)
        }
    }

    @Test
    fun `invoke does nothing when view does not exist`() = runTest {
        val routeValue = NavigationRoute.Current
        val jsonObjects = listOf(JsonObject(emptyMap()))

        val input = UpdateViewUseCase.Input(
            route = routeValue,
            jsonObjects = jsonObjects
        )

        everySuspend { navigationScreenStackRepository.getScreenOrNull(routeValue) } returns null

        sut.invoke(input)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            navigationScreenStackRepository.getScreenOrNull(routeValue)
        }
    }
}
