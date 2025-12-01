package com.tezov.tuucho.core.domain.business.usecase.withoutNetwork

import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.middleware.UpdateViewMiddleware
import com.tezov.tuucho.core.domain.business.middleware.UpdateViewMiddleware.Context
import com.tezov.tuucho.core.domain.business.mock.CoroutineTestScope
import com.tezov.tuucho.core.domain.business.protocol.MiddlewareExecutorProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.NavigationRepositoryProtocol
import com.tezov.tuucho.core.domain.business.protocol.screen.ScreenProtocol
import dev.mokkery.answering.calls
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifyNoMoreCalls
import dev.mokkery.verifySuspend
import kotlinx.serialization.json.JsonObject
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class UpdateViewUseCaseTest {
    private val coroutineTestScope = CoroutineTestScope()

    private lateinit var navigationScreenStackRepository: NavigationRepositoryProtocol.StackScreen
    private lateinit var middlewareExecutor: MiddlewareExecutorProtocol
    private lateinit var updateViewMiddlewares: List<UpdateViewMiddleware>

    private lateinit var sut: UpdateViewUseCase

    @BeforeTest
    fun setup() {
        coroutineTestScope.setup()
        navigationScreenStackRepository = mock()
        middlewareExecutor = mock()
        updateViewMiddlewares = listOf()
        sut = UpdateViewUseCase(
            coroutineScopes = coroutineTestScope.mock,
            navigationScreenStackRepository = navigationScreenStackRepository,
            middlewareExecutor = middlewareExecutor,
            updateViewMiddlewares = updateViewMiddlewares
        )
    }

    @AfterTest
    fun tearDown() {
        coroutineTestScope.verifyNoMoreCalls()
        verifyNoMoreCalls(
            navigationScreenStackRepository,
            middlewareExecutor
        )
    }

    @Test
    fun `invoke updates view when view exists`() = coroutineTestScope.run {
        val routeValue = NavigationRoute.Back
        val jsonObject = JsonObject(emptyMap())

        val input = UpdateViewUseCase.Input(
            route = routeValue,
            jsonObject = jsonObject
        )

        val screen = mock<ScreenProtocol>()

        everySuspend { navigationScreenStackRepository.getScreenOrNull(routeValue) } returns screen
        everySuspend { screen.update(any()) } returns Unit

        everySuspend {
            middlewareExecutor.process<Context, Unit>(any(), any())
        } calls { args ->
            val list = args.arg<List<UpdateViewMiddleware>>(0)
            val context = args.arg<Context>(1)
            list[0].process(context, null)
        }

        sut.invoke(input)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineTestScope.mock.useCase.await<Any>(any())
            middlewareExecutor.process<Context, Unit>(any(), any())
            navigationScreenStackRepository.getScreenOrNull(routeValue)
            coroutineTestScope.mock.renderer.await<Any>(any())
            screen.update(jsonObject)
        }
    }

    @Test
    fun `invoke does nothing when view does not exist`() = coroutineTestScope.run {
        val routeValue = NavigationRoute.Current
        val jsonObject = JsonObject(emptyMap())

        val input = UpdateViewUseCase.Input(
            route = routeValue,
            jsonObject = jsonObject
        )

        everySuspend { navigationScreenStackRepository.getScreenOrNull(routeValue) } returns null

        everySuspend {
            middlewareExecutor.process<Context, Unit>(any(), any())
        } calls { args ->
            val list = args.arg<List<UpdateViewMiddleware>>(0)
            val context = args.arg<Context>(1)
            list[0].process(context, null)
        }

        sut.invoke(input)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineTestScope.mock.useCase.await<Any>(any())
            middlewareExecutor.process<Context, Unit>(any(), any())
            navigationScreenStackRepository.getScreenOrNull(routeValue)
            coroutineTestScope.mock.renderer.await<Any>(any())
        }
    }
}
