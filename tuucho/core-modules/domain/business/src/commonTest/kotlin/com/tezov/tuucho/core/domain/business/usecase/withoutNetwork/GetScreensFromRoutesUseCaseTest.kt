package com.tezov.tuucho.core.domain.business.usecase.withoutNetwork

import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.protocol.repository.NavigationRepositoryProtocol
import com.tezov.tuucho.core.domain.business.protocol.screen.ScreenProtocol
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.GetScreensFromRoutesUseCase.Input
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.GetScreensFromRoutesUseCase.Output
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifyNoMoreCalls
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetScreensFromRoutesUseCaseTest {
    private lateinit var navigationStackScreenRepository: NavigationRepositoryProtocol.StackScreen

    private lateinit var sut: GetScreensFromRoutesUseCase

    @BeforeTest
    fun setup() {
        navigationStackScreenRepository = mock()
        sut = GetScreensFromRoutesUseCase(
            navigationStackScreenRepository = navigationStackScreenRepository
        )
    }

    @AfterTest
    fun tearDown() {
        verifyNoMoreCalls(navigationStackScreenRepository)
    }

    @Test
    fun `invoke returns screens from repository`() = runTest {
        val route1 = NavigationRoute.Url(id = "id1", value = "v1")
        val route2 = NavigationRoute.Url(id = "id2", value = "v2")
        val routes = listOf(route1, route2)

        val screen1 = mock<ScreenProtocol>()
        val screen2 = mock<ScreenProtocol>()
        val screens = listOf(screen1, screen2)

        val input = Input(
            routes = routes
        )

        everySuspend { navigationStackScreenRepository.getScreens(any()) } returns screens

        val result = sut.invoke(input)

        assertEquals(Output(screens = screens), result)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            navigationStackScreenRepository.getScreens(routes)
        }
    }
}
