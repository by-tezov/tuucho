package com.tezov.tuucho.core.domain.business.interaction.navigation

import com.tezov.tuucho.core.domain.business.mock.CoroutineTestScope
import com.tezov.tuucho.core.domain.business.protocol.repository.NavigationRepositoryProtocol
import com.tezov.tuucho.core.domain.business.protocol.screen.ScreenFactoryProtocol
import com.tezov.tuucho.core.domain.business.protocol.screen.ScreenProtocol
import dev.mokkery.answering.returns
import dev.mokkery.answering.sequentiallyReturns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.resetCalls
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifyNoMoreCalls
import dev.mokkery.verifySuspend
import kotlinx.serialization.json.buildJsonObject
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class NavigationStackScreenRepositoryTest {
    private val coroutineTestScope = CoroutineTestScope()
    private lateinit var navigationStackRouteRepository: NavigationRepositoryProtocol.StackRoute
    private lateinit var materialCacheRepository: NavigationRepositoryProtocol.MaterialCache

    private lateinit var screenFactory: ScreenFactoryProtocol
    private lateinit var sut: NavigationStackScreenRepository

    @BeforeTest
    fun setup() {
        coroutineTestScope.setup()
        materialCacheRepository = mock()
        navigationStackRouteRepository = mock()
        screenFactory = mock()
        sut = NavigationStackScreenRepository(
            coroutineScopes = coroutineTestScope.mock,
            navigationStackRouteRepository = navigationStackRouteRepository,
            screenFactory = screenFactory
        )

        everySuspend {
            materialCacheRepository.consumeNavigationDefinitionOptionObject(any())
        } returns buildJsonObject {}
    }

    @AfterTest
    fun tearDown() {
        coroutineTestScope.verifyNoMoreCalls()
        verifyNoMoreCalls(screenFactory, materialCacheRepository, navigationStackRouteRepository)
    }

    @Test
    fun `routes returns empty when no screens`() = coroutineTestScope.run {
        val result = sut.routes()
        assertEquals(emptyList(), result)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineTestScope.mock.default.withContext<Any>(any())
        }
    }

    @Test
    fun `push pushes a screen onto the stack`() = coroutineTestScope.run {
        val pushedRoute = NavigationRoute.Url("route-id", "route-url")
        val pushedScreen = mock<ScreenProtocol>()
        every { pushedScreen.route } returns pushedRoute
        everySuspend { screenFactory.create(pushedRoute) } returns pushedScreen

        sut.push(pushedRoute)

        val result = sut.routes()
        assertEquals(listOf(pushedRoute), result)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineTestScope.mock.default.withContext<Any>(any())
            screenFactory.create(pushedRoute)
            coroutineTestScope.mock.default.withContext<Any>(any())
        }
    }

    @Test
    fun `routes returns list of routes from screens`() = coroutineTestScope.run {
        val firstRoute = NavigationRoute.Url("first-id", "first-url")
        val secondRoute = NavigationRoute.Url("second-id", "second-url")

        val firstScreen = mock<ScreenProtocol>()
        val secondScreen = mock<ScreenProtocol>()

        every { firstScreen.route } returns firstRoute
        every { secondScreen.route } returns secondRoute

        everySuspend { screenFactory.create(any()) }
            .sequentiallyReturns(listOf(firstScreen, secondScreen))

        sut.push(firstRoute)
        sut.push(secondRoute)
        coroutineTestScope.resetCalls()

        val result = sut.routes()
        assertEquals(listOf(firstRoute, secondRoute), result)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            screenFactory.create(firstRoute)
            screenFactory.create(secondRoute)
            coroutineTestScope.mock.default.withContext<Any>(any())
        }
    }

    @Test
    fun `getScreens returns screens matching provided routes`() = coroutineTestScope.run {
        val routeMatchA = NavigationRoute.Url("match-a-id", "match-a-url")
        val routeNoMatch = NavigationRoute.Url("no-match-id", "no-match-url")
        val routeMatchB = NavigationRoute.Url("match-b-id", "match-b-url")

        val screenMatchA = mock<ScreenProtocol>()
        val screenNoMatch = mock<ScreenProtocol>()
        val screenMatchB = mock<ScreenProtocol>()

        every { screenMatchA.route } returns routeMatchA
        every { screenNoMatch.route } returns routeNoMatch
        every { screenMatchB.route } returns routeMatchB

        everySuspend { screenFactory.create(any()) }
            .sequentiallyReturns(listOf(screenMatchA, screenNoMatch, screenMatchB))

        sut.push(routeMatchA)
        sut.push(routeNoMatch)
        sut.push(routeMatchB)
        resetCalls(screenFactory)
        coroutineTestScope.resetCalls()

        val result = sut.getScreens(listOf(routeMatchA, routeMatchB))
        assertEquals(listOf(screenMatchA, screenMatchB), result)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineTestScope.mock.default.withContext<Any>(any())
        }
    }

    @Test
    fun `getScreenOrNull for Current returns last screen`() = coroutineTestScope.run {
        val firstRoute = NavigationRoute.Url("first-id", "first-url")
        val lastRoute = NavigationRoute.Url("last-id", "last-url")

        val firstScreen = mock<ScreenProtocol>()
        val lastScreen = mock<ScreenProtocol>()

        every { firstScreen.route } returns firstRoute
        every { lastScreen.route } returns lastRoute

        everySuspend { screenFactory.create(any()) }
            .sequentiallyReturns(listOf(firstScreen, lastScreen))

        sut.push(firstRoute)
        sut.push(lastRoute)
        resetCalls(screenFactory)
        coroutineTestScope.resetCalls()

        val result = sut.getScreenOrNull(NavigationRoute.Current)
        assertEquals(lastScreen, result)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineTestScope.mock.default.withContext<Any>(any())
        }
    }

    @Test
    fun `getScreenOrNull for Back returns prior screen`() = coroutineTestScope.run {
        val initialRoute = NavigationRoute.Url("initial-id", "initial-url")
        val nextRoute = NavigationRoute.Url("next-id", "next-url")

        val initialScreen = mock<ScreenProtocol>()
        val nextScreen = mock<ScreenProtocol>()

        every { initialScreen.route } returns initialRoute
        every { nextScreen.route } returns nextRoute

        everySuspend { screenFactory.create(any()) }
            .sequentiallyReturns(listOf(initialScreen, nextScreen))

        sut.push(initialRoute)
        sut.push(nextRoute)
        resetCalls(screenFactory)
        coroutineTestScope.resetCalls()

        val result = sut.getScreenOrNull(NavigationRoute.Back)
        assertEquals(initialScreen, result)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineTestScope.mock.default.withContext<Any>(any())
        }
    }

    @Test
    fun `getScreenOrNull returns matching screen by id`() = coroutineTestScope.run {
        val firstRoute = NavigationRoute.Url("first-id", "first-url")
        val targetRoute = NavigationRoute.Url("target-id", "target-url")

        val firstScreen = mock<ScreenProtocol>()
        val targetScreen = mock<ScreenProtocol>()

        every { firstScreen.route } returns firstRoute
        every { targetScreen.route } returns targetRoute

        everySuspend { screenFactory.create(any()) }
            .sequentiallyReturns(listOf(firstScreen, targetScreen))

        sut.push(firstRoute)
        sut.push(targetRoute)
        resetCalls(screenFactory)
        coroutineTestScope.resetCalls()

        val result = sut.getScreenOrNull(targetRoute)
        assertEquals(targetScreen, result)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineTestScope.mock.default.withContext<Any>(any())
        }
    }

    @Test
    fun `getScreensOrNull returns screens matching url`() = coroutineTestScope.run {
        val routeAlpha = NavigationRoute.Url("alpha-id", "shared-url")
        val routeBeta = NavigationRoute.Url("beta-id", "shared-url")
        val routeOther = NavigationRoute.Url("other-id", "other-url")

        val screenAlpha = mock<ScreenProtocol>()
        val screenBeta = mock<ScreenProtocol>()
        val screenOther = mock<ScreenProtocol>()

        every { screenAlpha.route } returns routeAlpha
        every { screenBeta.route } returns routeBeta
        every { screenOther.route } returns routeOther

        everySuspend { screenFactory.create(any()) }
            .sequentiallyReturns(listOf(screenAlpha, screenBeta, screenOther))

        sut.push(routeAlpha)
        sut.push(routeBeta)
        sut.push(routeOther)
        resetCalls(screenFactory)
        coroutineTestScope.resetCalls()

        val result = sut.getScreensOrNull("shared-url")
        assertEquals(listOf(screenAlpha, screenBeta), result)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineTestScope.mock.default.withContext<Any>(any())
        }
    }

    @Test
    fun `sync keeps only screens matching provided routes`() = coroutineTestScope.run {
        val routeKeepA = NavigationRoute.Url("keep-a-id", "keep-a-url")
        val routeRemove = NavigationRoute.Url("remove-id", "remove-url")
        val routeKeepB = NavigationRoute.Url("keep-b-id", "keep-b-url")

        val screenKeepA = mock<ScreenProtocol>()
        val screenRemove = mock<ScreenProtocol>()
        val screenKeepB = mock<ScreenProtocol>()

        every { screenKeepA.route } returns routeKeepA
        every { screenRemove.route } returns routeRemove
        every { screenKeepB.route } returns routeKeepB

        everySuspend { screenFactory.create(any()) }
            .sequentiallyReturns(listOf(screenKeepA, screenRemove, screenKeepB))

        sut.push(routeKeepA)
        sut.push(routeRemove)
        sut.push(routeKeepB)
        resetCalls(screenFactory)
        coroutineTestScope.resetCalls()

        everySuspend {
            navigationStackRouteRepository.routes()
        } returns listOf(routeKeepA, routeKeepB)

        sut.sync()

        val result = sut.routes()
        assertEquals(listOf(routeKeepA, routeKeepB), result)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            navigationStackRouteRepository.routes()
            coroutineTestScope.mock.default.withContext<Any>(any())
            coroutineTestScope.mock.default.withContext<Any>(any())
        }
    }

    @Test
    fun `getScreenOrNull returns null when route does not exist`() = coroutineTestScope.run {
        val existingRoute = NavigationRoute.Url("existing-id", "existing-url")
        val missingRoute = NavigationRoute.Url("missing-id", "missing-url")

        val existingScreen = mock<ScreenProtocol>()
        every { existingScreen.route } returns existingRoute

        everySuspend { screenFactory.create(any()) }
            .sequentiallyReturns(listOf(existingScreen))

        sut.push(existingRoute)
        resetCalls(screenFactory)
        coroutineTestScope.resetCalls()

        val result = sut.getScreenOrNull(missingRoute)
        assertEquals(null, result)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineTestScope.mock.default.withContext<Any>(any())
        }
    }

    @Test
    fun `getScreensOrNull returns empty when no route matches url`() = coroutineTestScope.run {
        val routeA = NavigationRoute.Url("id-a", "url-a")
        val routeB = NavigationRoute.Url("id-b", "url-b")

        val screenA = mock<ScreenProtocol>()
        val screenB = mock<ScreenProtocol>()

        every { screenA.route } returns routeA
        every { screenB.route } returns routeB

        everySuspend { screenFactory.create(any()) }
            .sequentiallyReturns(listOf(screenA, screenB))

        sut.push(routeA)
        sut.push(routeB)
        resetCalls(screenFactory)
        coroutineTestScope.resetCalls()

        val result = sut.getScreensOrNull("unknown-url")
        assertEquals(emptyList(), result)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineTestScope.mock.default.withContext<Any>(any())
        }
    }

    @Test
    fun `sync empty list clears all screens`() = coroutineTestScope.run {
        val routeA = NavigationRoute.Url("id-a", "url-a")
        val routeB = NavigationRoute.Url("id-b", "url-b")

        val screenA = mock<ScreenProtocol>()
        val screenB = mock<ScreenProtocol>()

        every { screenA.route } returns routeA
        every { screenB.route } returns routeB

        everySuspend { screenFactory.create(any()) }
            .sequentiallyReturns(listOf(screenA, screenB))

        sut.push(routeA)
        sut.push(routeB)
        resetCalls(screenFactory)
        coroutineTestScope.resetCalls()

        everySuspend {
            navigationStackRouteRepository.routes()
        } returns emptyList()

        sut.sync()

        val result = sut.routes()
        assertEquals(emptyList(), result)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            navigationStackRouteRepository.routes()
            coroutineTestScope.mock.default.withContext<Any>(any())
            coroutineTestScope.mock.default.withContext<Any>(any())
        }
    }

    @Test
    fun `push preserves screen order`() = coroutineTestScope.run {
        val routeFirst = NavigationRoute.Url("id-first", "url-first")
        val routeMiddle = NavigationRoute.Url("id-middle", "url-middle")
        val routeLast = NavigationRoute.Url("id-last", "url-last")

        val screenFirst = mock<ScreenProtocol>()
        val screenMiddle = mock<ScreenProtocol>()
        val screenLast = mock<ScreenProtocol>()

        every { screenFirst.route } returns routeFirst
        every { screenMiddle.route } returns routeMiddle
        every { screenLast.route } returns routeLast

        everySuspend { screenFactory.create(any()) }
            .sequentiallyReturns(listOf(screenFirst, screenMiddle, screenLast))

        sut.push(routeFirst)
        sut.push(routeMiddle)
        sut.push(routeLast)
        resetCalls(screenFactory)
        coroutineTestScope.resetCalls()

        val result = sut.routes()
        assertEquals(listOf(routeFirst, routeMiddle, routeLast), result)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineTestScope.mock.default.withContext<Any>(any())
        }
    }

    @Test
    fun `getScreens with duplicated routes returns unique screens`() = coroutineTestScope.run {
        val routeUnique = NavigationRoute.Url("id-unique", "url-unique")

        val screenUnique = mock<ScreenProtocol>()
        every { screenUnique.route } returns routeUnique

        everySuspend { screenFactory.create(any()) }
            .sequentiallyReturns(listOf(screenUnique))

        sut.push(routeUnique)
        resetCalls(screenFactory)
        coroutineTestScope.resetCalls()

        val result = sut.getScreens(listOf(routeUnique, routeUnique))
        assertEquals(listOf(screenUnique), result)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineTestScope.mock.default.withContext<Any>(any())
        }
    }

    @Test
    fun `screenRenderer process is called in forward order`() = coroutineTestScope.run {
        val routeA = NavigationRoute.Url("id-a", "url-a")
        val routeB = NavigationRoute.Url("id-b", "url-b")

        val screenA = mock<ScreenProtocol>()
        val screenB = mock<ScreenProtocol>()

        every { screenA.route } returns routeA
        every { screenB.route } returns routeB

        everySuspend { screenFactory.create(any()) }
            .sequentiallyReturns(listOf(screenA, screenB))

        sut.push(routeA)
        sut.push(routeB)
        coroutineTestScope.resetCalls()

        verifySuspend(VerifyMode.exhaustiveOrder) {
            screenFactory.create(routeA)
            screenFactory.create(routeB)
        }
    }
}
