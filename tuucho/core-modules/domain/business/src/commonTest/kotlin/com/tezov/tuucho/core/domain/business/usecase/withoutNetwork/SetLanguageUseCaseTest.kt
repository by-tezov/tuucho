package com.tezov.tuucho.core.domain.business.usecase.withoutNetwork

import com.tezov.tuucho.core.domain.business.interaction.navigation.NavigationRoute
import com.tezov.tuucho.core.domain.business.model.LanguageModelDomain
import com.tezov.tuucho.core.domain.business.protocol.repository.NavigationRepositoryProtocol
import com.tezov.tuucho.core.domain.business.protocol.repository.SystemPlatformRepositoryProtocol
import com.tezov.tuucho.core.domain.business.protocol.screen.ScreenProtocol
import com.tezov.tuucho.core.domain.business.usecase.withoutNetwork.SetLanguageUseCase.Input
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

class SetLanguageUseCaseTest {
    private lateinit var systemPlatformRepository: SystemPlatformRepositoryProtocol
    private lateinit var navigationStackRouteRepository: NavigationRepositoryProtocol.StackRoute
    private lateinit var navigationStackScreenRepository: NavigationRepositoryProtocol.StackScreen
    private lateinit var screenA: ScreenProtocol
    private lateinit var screenB: ScreenProtocol

    private lateinit var sut: SetLanguageUseCase

    @BeforeTest
    fun setup() {
        systemPlatformRepository = mock()
        navigationStackRouteRepository = mock()
        navigationStackScreenRepository = mock()
        sut = SetLanguageUseCase(
            platformRepository = systemPlatformRepository,
            navigationStackRouteRepository = navigationStackRouteRepository,
            navigationStackScreenRepository = navigationStackScreenRepository
        )
        screenA = mock()
        screenB = mock()
    }

    @AfterTest
    fun tearDown() {
        verifyNoMoreCalls(systemPlatformRepository, navigationStackRouteRepository, navigationStackScreenRepository, screenA, screenB)
    }

    @Test
    fun `invoke with language`() = runTest {
        val language = LanguageModelDomain(
            code = "en",
            country = "EN"
        )

        val input = Input(
            language = language,
        )

        val routeA = NavigationRoute.Url("id-a", "routeA")
        val routeB = NavigationRoute.Url("id-b", "routeB")

        everySuspend { systemPlatformRepository.setCurrentLanguage(any()) } returns Unit
        everySuspend { navigationStackRouteRepository.routes() } returns listOf(routeA, routeB)
        everySuspend { navigationStackScreenRepository.getScreens() } returns listOf(screenA, screenB)
        everySuspend { screenA.recreateViews() } returns Unit
        everySuspend { screenA.route } returns routeA
        everySuspend { screenB.recreateViews() } returns Unit
        everySuspend { screenB.route } returns routeB

        sut.invoke(input)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            systemPlatformRepository.setCurrentLanguage(language)
            navigationStackRouteRepository.routes()
            navigationStackScreenRepository.getScreens()
            screenB.route
            screenA.route
            screenB.recreateViews()
            screenA.recreateViews()
        }
    }
}
