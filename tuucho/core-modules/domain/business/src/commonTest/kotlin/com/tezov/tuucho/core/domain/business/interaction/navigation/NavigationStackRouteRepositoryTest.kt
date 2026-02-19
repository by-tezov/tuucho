package com.tezov.tuucho.core.domain.business.interaction.navigation

import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.business.mock.CoroutineTestScope
import com.tezov.tuucho.core.domain.business.protocol.repository.NavigationRepositoryProtocol
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.resetCalls
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifyNoMoreCalls
import dev.mokkery.verifySuspend
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class NavigationStackRouteRepositoryTest {
    private val coroutineTestScope = CoroutineTestScope()
    private lateinit var materialCacheRepository: NavigationRepositoryProtocol.MaterialCache

    private lateinit var sut: NavigationStackRouteRepository

    @BeforeTest
    fun setup() {
        coroutineTestScope.setup()
        materialCacheRepository = mock()

        sut = NavigationStackRouteRepository(
            coroutineScopes = coroutineTestScope.mock,
            materialCacheRepository = materialCacheRepository
        )
    }

    @AfterTest
    fun tearDown() {
        coroutineTestScope.verifyNoMoreCalls()
        verifyNoMoreCalls(materialCacheRepository)
    }

    @Test
    fun `currentRoute empty returns null`() = coroutineTestScope.run {
        val result = sut.currentRoute()

        assertNull(result)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineTestScope.mock.default.withContext<Any>(any())
        }
    }

    @Test
    fun `priorRoute empty returns null`() = coroutineTestScope.run {
        val result = sut.priorRoute()

        assertNull(result)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineTestScope.mock.default.withContext<Any>(any())
        }
    }

    @Test
    fun `routes empty returns empty list`() = coroutineTestScope.run {
        val result = sut.routes()

        assertEquals(emptyList(), result)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineTestScope.mock.default.withContext<Any>(any())
        }
    }

    @Test
    fun `forward pushes route`() = coroutineTestScope.run {
        val route = NavigationRoute.Url("id", "url")

        everySuspend {
            materialCacheRepository.prepareNavigationConsumable(any())
        } returns Unit

        everySuspend {
            materialCacheRepository.consumeNavigationDefinitionOptionObject(any())
        } returns null

        everySuspend {
            materialCacheRepository.bindComponentObjectCache(any())
        } returns Unit

        sut.forward(route)

        val result = sut.routes()
        assertEquals(listOf(route), result)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            materialCacheRepository.prepareNavigationConsumable(route.value)
            materialCacheRepository.consumeNavigationDefinitionOptionObject(route.value)
            coroutineTestScope.mock.default.withContext<Any>(any())
            materialCacheRepository.bindComponentObjectCache(route)
            coroutineTestScope.mock.default.withContext<Any>(any())
        }
    }

    @Test
    fun `forward with clearStack clears everything`() = coroutineTestScope.run {
        val route1 = NavigationRoute.Url("id-1", "url-1")
        val route2 = NavigationRoute.Url("id-2", "url-2")

        everySuspend {
            materialCacheRepository.prepareNavigationConsumable(any())
        } returns Unit

        everySuspend {
            materialCacheRepository.consumeNavigationDefinitionOptionObject(any())
        } returns null

        everySuspend {
            materialCacheRepository.bindComponentObjectCache(any())
        } returns Unit

        everySuspend {
            materialCacheRepository.consumeNavigationDefinitionOptionObject(route2.value)
        } returns buildJsonObject {
            put("clear-stack", true)
        }

        sut.forward(route1)
        resetCalls(materialCacheRepository)
        coroutineTestScope.resetCalls()

        sut.forward(route = route2)

        val result = sut.routes()
        assertEquals(listOf(route2), result)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            materialCacheRepository.prepareNavigationConsumable(route2.value)
            materialCacheRepository.consumeNavigationDefinitionOptionObject(route2.value)
            coroutineTestScope.mock.default.withContext<Any>(any())
            materialCacheRepository.bindComponentObjectCache(route2)
            coroutineTestScope.mock.default.withContext<Any>(any())
        }
    }

    @Test
    fun `backward with Back removes last`() = coroutineTestScope.run {
        val route1 = NavigationRoute.Url("id-1", "url-1")
        val route2 = NavigationRoute.Url("id-2", "url-2")

        everySuspend {
            materialCacheRepository.prepareNavigationConsumable(any())
        } returns Unit

        everySuspend {
            materialCacheRepository.consumeNavigationDefinitionOptionObject(any())
        } returns null

        everySuspend {
            materialCacheRepository.bindComponentObjectCache(any())
        } returns Unit

        sut.forward(route1)
        sut.forward(route2)
        resetCalls(materialCacheRepository)
        coroutineTestScope.resetCalls()

        sut.backward(NavigationRoute.Back)

        val result = sut.routes()
        assertEquals(listOf(route1), result)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineTestScope.mock.default.withContext<Any>(any())
            coroutineTestScope.mock.default.withContext<Any>(any())
        }
    }

    @Test
    fun `backward with Finish clears stack`() = coroutineTestScope.run {
        val route1 = NavigationRoute.Url("id-1", "url-1")
        val route2 = NavigationRoute.Url("id-2", "url-2")

        everySuspend {
            materialCacheRepository.prepareNavigationConsumable(any())
        } returns Unit

        everySuspend {
            materialCacheRepository.consumeNavigationDefinitionOptionObject(any())
        } returns null

        everySuspend {
            materialCacheRepository.bindComponentObjectCache(any())
        } returns Unit

        sut.forward(route1)
        sut.forward(route2)
        resetCalls(materialCacheRepository)
        coroutineTestScope.resetCalls()

        val result = sut.backward(NavigationRoute.Finish)
        assertNull(result)

        assertEquals(emptyList(), sut.routes())

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineTestScope.mock.default.withContext<Any>(any())
            coroutineTestScope.mock.default.withContext<Any>(any())
        }
    }

    @Test
    fun `backward with invalid route throws`() = coroutineTestScope.run {
        assertFailsWith<DomainException.Default> {
            sut.backward(NavigationRoute.Current)
        }

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineTestScope.mock.default.withContext<Any>(any())
        }
    }

    @Test
    fun `backward Url throws`() = coroutineTestScope.run {
        val route = NavigationRoute.Url("id", "url")

        assertFailsWith<DomainException.Default> {
            sut.backward(route)
        }

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineTestScope.mock.default.withContext<Any>(any())
        }
    }

    @Test
    fun `forward with single removes previous matching`() = coroutineTestScope.run {
        val route1 = NavigationRoute.Url("id-1", "url")
        val route2 = NavigationRoute.Url("id-2", "url")

        everySuspend {
            materialCacheRepository.prepareNavigationConsumable(any())
        } returns Unit

        everySuspend {
            materialCacheRepository.consumeNavigationDefinitionOptionObject(any())
        } returns null

        everySuspend {
            materialCacheRepository.bindComponentObjectCache(any())
        } returns Unit

        everySuspend {
            materialCacheRepository.consumeNavigationDefinitionOptionObject(route2.value)
        } returns buildJsonObject {
            put("single", true)
        }

        sut.forward(route1)
        resetCalls(materialCacheRepository)
        coroutineTestScope.resetCalls()

        sut.forward(route = route2)

        val result = sut.routes()
        assertEquals(listOf(route2), result)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            materialCacheRepository.prepareNavigationConsumable(route2.value)
            materialCacheRepository.consumeNavigationDefinitionOptionObject(route2.value)
            coroutineTestScope.mock.default.withContext<Any>(any())
            materialCacheRepository.bindComponentObjectCache(route2)
            coroutineTestScope.mock.default.withContext<Any>(any())
        }
    }

    @Test
    fun `forward with invalid reuse throws`() = coroutineTestScope.run {
        val route = NavigationRoute.Url("id", "url")

        everySuspend {
            materialCacheRepository.prepareNavigationConsumable(any())
        } returns Unit

        everySuspend {
            materialCacheRepository.consumeNavigationDefinitionOptionObject(any())
        } returns buildJsonObject {
            put("reuse", "invalid")
        }

        assertFailsWith<DomainException.Default> {
            sut.forward(route = route)
        }

        verifySuspend(VerifyMode.exhaustiveOrder) {
            materialCacheRepository.prepareNavigationConsumable(route.value)
            materialCacheRepository.consumeNavigationDefinitionOptionObject(route.value)
            coroutineTestScope.mock.default.withContext<Any>(any())
        }
    }

    @Test
    fun `popupTo removes items until matching`() = coroutineTestScope.run {
        val route = NavigationRoute.Url("id", "url")
        val route1 = NavigationRoute.Url("id-1", "url-1")
        val route2 = NavigationRoute.Url("id-2", "url-2")
        val route3 = NavigationRoute.Url("id-3", "url-3")

        everySuspend {
            materialCacheRepository.prepareNavigationConsumable(any())
        } returns Unit

        everySuspend {
            materialCacheRepository.consumeNavigationDefinitionOptionObject(any())
        } returns null

        everySuspend {
            materialCacheRepository.bindComponentObjectCache(any())
        } returns Unit
        everySuspend {
            materialCacheRepository.consumeNavigationDefinitionOptionObject(route.value)
        } returns buildJsonObject {
            put("pop-up-to", buildJsonObject {
                put("url", "url-2")
                put("inclusive", false)
            })
        }

        sut.forward(route1)
        sut.forward(route2)
        sut.forward(route3)
        resetCalls(materialCacheRepository)
        coroutineTestScope.resetCalls()

        sut.forward(route = route)

        val result = sut.routes()
        assertEquals(listOf(route1, route2, route), result)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            materialCacheRepository.prepareNavigationConsumable(route.value)
            materialCacheRepository.consumeNavigationDefinitionOptionObject(route.value)
            coroutineTestScope.mock.default.withContext<Any>(any())
            materialCacheRepository.bindComponentObjectCache(route)
            coroutineTestScope.mock.default.withContext<Any>(any())
        }
    }

    @Test
    fun `forward with reuse last reuses last matching`() = coroutineTestScope.run {
        val route = NavigationRoute.Url("id-X", "url")
        val routeInitial1 = NavigationRoute.Url("id-1", "url")
        val routeInitial2 = NavigationRoute.Url("id-2", "url")

        everySuspend {
            materialCacheRepository.prepareNavigationConsumable(any())
        } returns Unit

        everySuspend {
            materialCacheRepository.consumeNavigationDefinitionOptionObject(any())
        } returns null

        everySuspend {
            materialCacheRepository.bindComponentObjectCache(any())
        } returns Unit

        sut.forward(routeInitial1)
        sut.forward(routeInitial2)
        resetCalls(materialCacheRepository)
        coroutineTestScope.resetCalls()

        everySuspend {
            materialCacheRepository.consumeNavigationDefinitionOptionObject(route.value)
        } returns buildJsonObject {
            put("reuse", "last")
        }

        val forwardResult = sut.forward(route = route)

        assertEquals(routeInitial2, forwardResult)

        val result = sut.routes()
        assertEquals(listOf(routeInitial1, routeInitial2), result)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            materialCacheRepository.prepareNavigationConsumable(route.value)
            materialCacheRepository.consumeNavigationDefinitionOptionObject(route.value)
            coroutineTestScope.mock.default.withContext<Any>(any())
            materialCacheRepository.bindComponentObjectCache(route)
            coroutineTestScope.mock.default.withContext<Any>(any())
        }
    }

    @Test
    fun `forward with reuse first reuses first matching`() = coroutineTestScope.run {
        val route = NavigationRoute.Url("id-X", "url")
        val routeInitial1 = NavigationRoute.Url("id-1", "url")
        val routeInitial2 = NavigationRoute.Url("id-2", "url")

        everySuspend {
            materialCacheRepository.prepareNavigationConsumable(any())
        } returns Unit

        everySuspend {
            materialCacheRepository.consumeNavigationDefinitionOptionObject(any())
        } returns null

        everySuspend {
            materialCacheRepository.bindComponentObjectCache(any())
        } returns Unit

        sut.forward(routeInitial1)
        sut.forward(routeInitial2)
        resetCalls(materialCacheRepository)
        coroutineTestScope.resetCalls()

        everySuspend {
            materialCacheRepository.consumeNavigationDefinitionOptionObject(route.value)
        } returns buildJsonObject {
            put("reuse", "first")
        }

        val forwardResult = sut.forward(route = route)

        assertEquals(routeInitial1, forwardResult)

        val result = sut.routes()
        assertEquals(listOf(routeInitial2, routeInitial1), result)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            materialCacheRepository.prepareNavigationConsumable(route.value)
            materialCacheRepository.consumeNavigationDefinitionOptionObject(route.value)
            coroutineTestScope.mock.default.withContext<Any>(any())
            materialCacheRepository.bindComponentObjectCache(route)
            coroutineTestScope.mock.default.withContext<Any>(any())
        }
    }

    @Test
    fun `forward with reuse true behaves as last`() = coroutineTestScope.run {
        val route = NavigationRoute.Url("id-X", "url")
        val routeInitial1 = NavigationRoute.Url("id-1", "url")
        val routeInitial2 = NavigationRoute.Url("id-2", "url")

        everySuspend {
            materialCacheRepository.prepareNavigationConsumable(any())
        } returns Unit

        everySuspend {
            materialCacheRepository.consumeNavigationDefinitionOptionObject(any())
        } returns null

        everySuspend {
            materialCacheRepository.bindComponentObjectCache(any())
        } returns Unit

        sut.forward(routeInitial1)
        sut.forward(routeInitial2)
        resetCalls(materialCacheRepository)
        coroutineTestScope.resetCalls()

        everySuspend {
            materialCacheRepository.consumeNavigationDefinitionOptionObject(route.value)
        } returns buildJsonObject {
            put("reuse", "true")
        }

        val forwardResult = sut.forward(route = route)

        assertEquals(routeInitial2, forwardResult)

        val result = sut.routes()
        assertEquals(listOf(routeInitial1, routeInitial2), result)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            materialCacheRepository.prepareNavigationConsumable(route.value)
            materialCacheRepository.consumeNavigationDefinitionOptionObject(route.value)
            coroutineTestScope.mock.default.withContext<Any>(any())
            materialCacheRepository.bindComponentObjectCache(route)
            coroutineTestScope.mock.default.withContext<Any>(any())
        }
    }

    @Test
    fun `popupTo inclusive true removes including target`() = coroutineTestScope.run {
        val route = NavigationRoute.Url("id-X", "url-x")
        val route1 = NavigationRoute.Url("id-1", "url-1")
        val route2 = NavigationRoute.Url("id-2", "url-2")
        val route3 = NavigationRoute.Url("id-3", "url-3")

        everySuspend {
            materialCacheRepository.prepareNavigationConsumable(any())
        } returns Unit

        everySuspend {
            materialCacheRepository.consumeNavigationDefinitionOptionObject(any())
        } returns null

        everySuspend {
            materialCacheRepository.bindComponentObjectCache(any())
        } returns Unit

        sut.forward(route1)
        sut.forward(route2)
        sut.forward(route3)
        resetCalls(materialCacheRepository)
        coroutineTestScope.resetCalls()

        everySuspend {
            materialCacheRepository.consumeNavigationDefinitionOptionObject(route.value)
        } returns buildJsonObject {
            put("pop-up-to", buildJsonObject {
                put("url", "url-2")
                put("inclusive", true)
            })
        }

        sut.forward(route = route)

        val result = sut.routes()
        assertEquals(listOf(route1, route), result)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            materialCacheRepository.prepareNavigationConsumable(route.value)
            materialCacheRepository.consumeNavigationDefinitionOptionObject(route.value)
            coroutineTestScope.mock.default.withContext<Any>(any())
            materialCacheRepository.bindComponentObjectCache(route)
            coroutineTestScope.mock.default.withContext<Any>(any())
        }
    }

    @Test
    fun `popupTo greedy false uses last matching`() = coroutineTestScope.run {
        val route = NavigationRoute.Url("id-X", "url-x")
        val routeFirst = NavigationRoute.Url("id-a", "shared")
        val routeSecond = NavigationRoute.Url("id-b", "shared")
        val routeThird = NavigationRoute.Url("id-c", "other")

        everySuspend {
            materialCacheRepository.prepareNavigationConsumable(any())
        } returns Unit

        everySuspend {
            materialCacheRepository.consumeNavigationDefinitionOptionObject(any())
        } returns null

        everySuspend {
            materialCacheRepository.bindComponentObjectCache(any())
        } returns Unit

        sut.forward(routeFirst)
        sut.forward(routeSecond)
        sut.forward(routeThird)
        resetCalls(materialCacheRepository)
        coroutineTestScope.resetCalls()

        everySuspend {
            materialCacheRepository.consumeNavigationDefinitionOptionObject(route.value)
        } returns buildJsonObject {
            put("pop-up-to", buildJsonObject {
                put("url", "shared")
                put("greedy", false)
            })
        }

        sut.forward(route = route)

        val result = sut.routes()
        assertEquals(listOf(routeFirst, routeSecond, route), result)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            materialCacheRepository.prepareNavigationConsumable(route.value)
            materialCacheRepository.consumeNavigationDefinitionOptionObject(route.value)
            coroutineTestScope.mock.default.withContext<Any>(any())
            materialCacheRepository.bindComponentObjectCache(route)
            coroutineTestScope.mock.default.withContext<Any>(any())
        }
    }

    @Test
    fun `popupTo route not found throws`() = coroutineTestScope.run {
        val route = NavigationRoute.Url("id-X", "url-x")
        val existingRoute = NavigationRoute.Url("id-1", "url-existing")

        everySuspend {
            materialCacheRepository.prepareNavigationConsumable(any())
        } returns Unit

        everySuspend {
            materialCacheRepository.consumeNavigationDefinitionOptionObject(any())
        } returns null

        everySuspend {
            materialCacheRepository.bindComponentObjectCache(any())
        } returns Unit

        sut.forward(existingRoute)
        resetCalls(materialCacheRepository)
        coroutineTestScope.resetCalls()

        everySuspend {
            materialCacheRepository.consumeNavigationDefinitionOptionObject(route.value)
        } returns buildJsonObject {
            put("pop-up-to", buildJsonObject {
                put("url", "shared")
                put("greedy", false)
            })
        }

        assertFailsWith<DomainException.Default> {
            sut.forward(route)
        }

        verifySuspend(VerifyMode.exhaustiveOrder) {
            materialCacheRepository.prepareNavigationConsumable(route.value)
            materialCacheRepository.consumeNavigationDefinitionOptionObject(route.value)
            coroutineTestScope.mock.default.withContext<Any>(any())
        }
    }

    @Test
    fun `single removes all previous matching routes`() = coroutineTestScope.run {
        val route = NavigationRoute.Url("id-X", "url-shared")
        val routeFirst = NavigationRoute.Url("id-1", "url-shared")
        val routeSecond = NavigationRoute.Url("id-2", "url-shared")

        everySuspend {
            materialCacheRepository.prepareNavigationConsumable(any())
        } returns Unit

        everySuspend {
            materialCacheRepository.consumeNavigationDefinitionOptionObject(any())
        } returns null

        everySuspend {
            materialCacheRepository.bindComponentObjectCache(any())
        } returns Unit

        sut.forward(routeFirst)
        sut.forward(routeSecond)
        resetCalls(materialCacheRepository)
        coroutineTestScope.resetCalls()

        everySuspend {
            materialCacheRepository.consumeNavigationDefinitionOptionObject(route.value)
        } returns buildJsonObject {
            put("single", true)
        }

        sut.forward(route)

        val result = sut.routes()
        assertEquals(listOf(route), result)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            materialCacheRepository.prepareNavigationConsumable(route.value)
            materialCacheRepository.consumeNavigationDefinitionOptionObject(route.value)
            coroutineTestScope.mock.default.withContext<Any>(any())
            materialCacheRepository.bindComponentObjectCache(route)
            coroutineTestScope.mock.default.withContext<Any>(any())
        }
    }

    @Test
    fun `forward returns null when route is reused`() = coroutineTestScope.run {
        val route = NavigationRoute.Url("id-X", "url")
        val routeInitial = NavigationRoute.Url("id-1", "url")

        everySuspend {
            materialCacheRepository.prepareNavigationConsumable(any())
        } returns Unit

        everySuspend {
            materialCacheRepository.consumeNavigationDefinitionOptionObject(any())
        } returns null

        everySuspend {
            materialCacheRepository.bindComponentObjectCache(any())
        } returns Unit

        sut.forward(routeInitial)
        resetCalls(materialCacheRepository)
        coroutineTestScope.resetCalls()

        everySuspend {
            materialCacheRepository.consumeNavigationDefinitionOptionObject(route.value)
        } returns buildJsonObject {
            put("reuse", "last")
        }

        val forwardResult = sut.forward(route)

        assertEquals(routeInitial, forwardResult)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            materialCacheRepository.prepareNavigationConsumable(route.value)
            materialCacheRepository.consumeNavigationDefinitionOptionObject(route.value)
            coroutineTestScope.mock.default.withContext<Any>(any())
            materialCacheRepository.bindComponentObjectCache(route)
        }
    }

    @Test
    fun `priorRoute returns previous route`() = coroutineTestScope.run {
        val firstRoute = NavigationRoute.Url("id-1", "url-1")
        val secondRoute = NavigationRoute.Url("id-2", "url-2")

        everySuspend {
            materialCacheRepository.prepareNavigationConsumable(any())
        } returns Unit

        everySuspend {
            materialCacheRepository.consumeNavigationDefinitionOptionObject(any())
        } returns null

        everySuspend {
            materialCacheRepository.bindComponentObjectCache(any())
        } returns Unit

        sut.forward(firstRoute)
        sut.forward(secondRoute)
        resetCalls(materialCacheRepository)
        coroutineTestScope.resetCalls()

        val result = sut.priorRoute()
        assertEquals(firstRoute, result)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineTestScope.mock.default.withContext<Any>(any())
        }
    }

    @Test
    fun `currentRoute returns last route`() = coroutineTestScope.run {
        val firstRoute = NavigationRoute.Url("id-1", "url-1")
        val lastRoute = NavigationRoute.Url("id-2", "url-2")

        everySuspend {
            materialCacheRepository.prepareNavigationConsumable(any())
        } returns Unit

        everySuspend {
            materialCacheRepository.consumeNavigationDefinitionOptionObject(any())
        } returns null

        everySuspend {
            materialCacheRepository.bindComponentObjectCache(any())
        } returns Unit

        sut.forward(firstRoute)
        sut.forward(lastRoute)
        resetCalls(materialCacheRepository)
        coroutineTestScope.resetCalls()

        val result = sut.currentRoute()
        assertEquals(lastRoute, result)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineTestScope.mock.default.withContext<Any>(any())
        }
    }

    @Test
    fun `mixed forward backward forward sequence`() = coroutineTestScope.run {
        val route1 = NavigationRoute.Url("id-1", "url-1")
        val route2 = NavigationRoute.Url("id-2", "url-2")
        val route3 = NavigationRoute.Url("id-3", "url-3")

        everySuspend {
            materialCacheRepository.prepareNavigationConsumable(any())
        } returns Unit

        everySuspend {
            materialCacheRepository.consumeNavigationDefinitionOptionObject(any())
        } returns null

        everySuspend {
            materialCacheRepository.bindComponentObjectCache(any())
        } returns Unit

        sut.forward(route1)
        sut.forward(route2)
        sut.backward(NavigationRoute.Back)
        sut.forward(route3)
        resetCalls(materialCacheRepository)
        coroutineTestScope.resetCalls()

        val result = sut.routes()
        assertEquals(listOf(route1, route3), result)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineTestScope.mock.default.withContext<Any>(any())
        }
    }

    @Test
    fun `popupTo missing url throws`() = coroutineTestScope.run {
        val route = NavigationRoute.Url("id-1", "url-1")

        everySuspend {
            materialCacheRepository.prepareNavigationConsumable(any())
        } returns Unit

        everySuspend {
            materialCacheRepository.consumeNavigationDefinitionOptionObject(any())
        } returns buildJsonObject {
            put("pop-up-to", buildJsonObject {
                // no url
            })
        }

        assertFailsWith<DomainException.Default> {
            sut.forward(route = route)
        }

        verifySuspend(VerifyMode.exhaustiveOrder) {
            materialCacheRepository.prepareNavigationConsumable(route.value)
            materialCacheRepository.consumeNavigationDefinitionOptionObject(route.value)
            coroutineTestScope.mock.default.withContext<Any>(any())
        }
    }

    @Test
    fun `single missing keeps previous routes`() = coroutineTestScope.run {
        val route1 = NavigationRoute.Url("id-1", "url")
        val route2 = NavigationRoute.Url("id-2", "url")

        everySuspend {
            materialCacheRepository.prepareNavigationConsumable(any())
        } returns Unit

        everySuspend {
            materialCacheRepository.consumeNavigationDefinitionOptionObject(any())
        } returns null

        everySuspend {
            materialCacheRepository.bindComponentObjectCache(any())
        } returns Unit

        sut.forward(route1)
        resetCalls(materialCacheRepository)
        coroutineTestScope.resetCalls()

        everySuspend {
            materialCacheRepository.consumeNavigationDefinitionOptionObject(any())
        } returns buildJsonObject { }

        sut.forward(route = route2)

        val result = sut.routes()
        assertEquals(listOf(route1, route2), result)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            materialCacheRepository.prepareNavigationConsumable(route2.value)
            materialCacheRepository.consumeNavigationDefinitionOptionObject(route2.value)
            coroutineTestScope.mock.default.withContext<Any>(any())
            materialCacheRepository.bindComponentObjectCache(route2)
            coroutineTestScope.mock.default.withContext<Any>(any())
        }
    }

    @Test
    fun `backward Back on empty returns null`() = coroutineTestScope.run {
        val result = sut.backward(NavigationRoute.Back)
        assertNull(result)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineTestScope.mock.default.withContext<Any>(any())
        }
    }

    @Test
    fun `popupTo greedy true inclusive false`() = coroutineTestScope.run {
        val route = NavigationRoute.Url("id-X", "url-X")
        val route1 = NavigationRoute.Url("id-1", "shared")
        val route2 = NavigationRoute.Url("id-2", "shared")
        val route3 = NavigationRoute.Url("id-3", "other")

        everySuspend {
            materialCacheRepository.prepareNavigationConsumable(any())
        } returns Unit

        everySuspend {
            materialCacheRepository.consumeNavigationDefinitionOptionObject(any())
        } returns null

        everySuspend {
            materialCacheRepository.bindComponentObjectCache(any())
        } returns Unit

        sut.forward(route1)
        sut.forward(route2)
        sut.forward(route3)
        resetCalls(materialCacheRepository)
        coroutineTestScope.resetCalls()

        everySuspend {
            materialCacheRepository.consumeNavigationDefinitionOptionObject(any())
        } returns buildJsonObject {
            put("pop-up-to", buildJsonObject {
                put("url", "shared")
                put("inclusive", false)
                put("greedy", true)
            })
        }

        sut.forward(route = route)

        val result = sut.routes()
        assertEquals(listOf(route1, route), result)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            materialCacheRepository.prepareNavigationConsumable(route.value)
            materialCacheRepository.consumeNavigationDefinitionOptionObject(route.value)
            coroutineTestScope.mock.default.withContext<Any>(any())
            materialCacheRepository.bindComponentObjectCache(route)
            coroutineTestScope.mock.default.withContext<Any>(any())
        }
    }

    @Test
    fun `forward reuse last does nothing when no matching route`() = coroutineTestScope.run {
        val route = NavigationRoute.Url("id-2", "url-B")
        val route1 = NavigationRoute.Url("id-1", "url-A")

        everySuspend {
            materialCacheRepository.prepareNavigationConsumable(any())
        } returns Unit

        everySuspend {
            materialCacheRepository.consumeNavigationDefinitionOptionObject(any())
        } returns null

        everySuspend {
            materialCacheRepository.bindComponentObjectCache(any())
        } returns Unit

        sut.forward(route1)
        resetCalls(materialCacheRepository)
        coroutineTestScope.resetCalls()

        everySuspend {
            materialCacheRepository.consumeNavigationDefinitionOptionObject(any())
        } returns buildJsonObject {
            put("reuse", "last")
        }

        sut.forward(route = route)

        val result = sut.routes()
        assertEquals(
            listOf(
                NavigationRoute.Url("id-1", "url-A"),
                NavigationRoute.Url("id-2", "url-B")
            ),
            result
        )

        verifySuspend(VerifyMode.exhaustiveOrder) {
            materialCacheRepository.prepareNavigationConsumable(route.value)
            materialCacheRepository.consumeNavigationDefinitionOptionObject(route.value)
            coroutineTestScope.mock.default.withContext<Any>(any())
            materialCacheRepository.bindComponentObjectCache(route)
            coroutineTestScope.mock.default.withContext<Any>(any())
        }
    }

    @Test
    fun `forward reuse first does nothing when no matching route`() = coroutineTestScope.run {
        val route1 = NavigationRoute.Url("id-1", "url-A")
        val route2 = NavigationRoute.Url("id-2", "url-B")

        everySuspend {
            materialCacheRepository.prepareNavigationConsumable(any())
        } returns Unit

        everySuspend {
            materialCacheRepository.consumeNavigationDefinitionOptionObject(any())
        } returns null

        everySuspend {
            materialCacheRepository.bindComponentObjectCache(any())
        } returns Unit

        sut.forward(route1)
        resetCalls(materialCacheRepository)
        coroutineTestScope.resetCalls()

        everySuspend {
            materialCacheRepository.consumeNavigationDefinitionOptionObject(any())
        } returns buildJsonObject {
            put("reuse", "first")
        }

        sut.forward(route = route2)

        val result = sut.routes()

        assertEquals(listOf(route1, route2), result)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            materialCacheRepository.prepareNavigationConsumable(route2.value)
            materialCacheRepository.consumeNavigationDefinitionOptionObject(route2.value)
            coroutineTestScope.mock.default.withContext<Any>(any())
            materialCacheRepository.bindComponentObjectCache(route2)
            coroutineTestScope.mock.default.withContext<Any>(any())
        }
    }

    @Test
    fun `forward with single false keeps previous matching`() = coroutineTestScope.run {
        val route1 = NavigationRoute.Url("id-1", "url")
        val route2 = NavigationRoute.Url("id-2", "url")

        everySuspend {
            materialCacheRepository.prepareNavigationConsumable(any())
        } returns Unit

        everySuspend {
            materialCacheRepository.consumeNavigationDefinitionOptionObject(any())
        } returns null

        everySuspend {
            materialCacheRepository.bindComponentObjectCache(any())
        } returns Unit

        sut.forward(route1)
        resetCalls(materialCacheRepository)
        coroutineTestScope.resetCalls()

        everySuspend {
            materialCacheRepository.consumeNavigationDefinitionOptionObject(any())
        } returns buildJsonObject {
            put("single", false)
        }

        sut.forward(route = route2)

        val result = sut.routes()
        assertEquals(listOf(route1, route2), result)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            materialCacheRepository.prepareNavigationConsumable(route2.value)
            materialCacheRepository.consumeNavigationDefinitionOptionObject(route2.value)
            coroutineTestScope.mock.default.withContext<Any>(any())
            materialCacheRepository.bindComponentObjectCache(route2)
            coroutineTestScope.mock.default.withContext<Any>(any())
        }
    }
}
