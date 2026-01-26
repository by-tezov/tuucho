package com.tezov.tuucho.core.domain.business.usecase.withoutNetwork

import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.protocol.repository.NavigationRepositoryProtocol
import com.tezov.tuucho.core.domain.business.protocol.screen.ScreenProtocol
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.GetScreenOrNullUseCase.Input
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.GetScreenOrNullUseCase.Output
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

class GetScreenOrNullUseCaseTest {
    private lateinit var navigationStackScreenRepository: NavigationRepositoryProtocol.StackScreen

    private lateinit var sut: GetScreenOrNullUseCase

    @BeforeTest
    fun setup() {
        navigationStackScreenRepository = mock()
        sut = GetScreenOrNullUseCase(
            navigationStackScreenRepository = navigationStackScreenRepository
        )
    }

    @AfterTest
    fun tearDown() {
        verifyNoMoreCalls(navigationStackScreenRepository)
    }

    @Test
    fun `invoke returns screen when repository provides one`() = runTest {
        val routeValue = NavigationRoute.Url(id = "id1", value = "value1")
        val screenMock = mock<ScreenProtocol>()

        val input = Input(route = routeValue)

        everySuspend { navigationStackScreenRepository.getScreenOrNull(any()) } returns screenMock

        val output = sut.invoke(input)

        assertEquals(Output(screen = screenMock), output)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            navigationStackScreenRepository.getScreenOrNull(routeValue)
        }
    }

    @Test
    fun `invoke returns null when repository returns null`() = runTest {
        val routeValue = NavigationRoute.Finish
        val input = Input(route = routeValue)

        everySuspend { navigationStackScreenRepository.getScreenOrNull(any()) } returns null

        val output = sut.invoke(input)

        assertEquals(Output(screen = null), output)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            navigationStackScreenRepository.getScreenOrNull(routeValue)
        }
    }
}
