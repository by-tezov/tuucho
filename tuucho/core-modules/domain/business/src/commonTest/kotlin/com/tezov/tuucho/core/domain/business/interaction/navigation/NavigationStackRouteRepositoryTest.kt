package com.tezov.tuucho.core.domain.business.interaction.navigation

import com.tezov.tuucho.core.domain.business.exception.DomainException
import com.tezov.tuucho.core.domain.business.mock.CoroutineTestScope
import dev.mokkery.verify.VerifyMode
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
    private lateinit var sut: NavigationStackRouteRepository

    @BeforeTest
    fun setup() {
        coroutineTestScope.setup()

        sut = NavigationStackRouteRepository(
            coroutineScopes = coroutineTestScope.mock
        )
    }

    @AfterTest
    fun tearDown() {
        coroutineTestScope.verifyNoMoreCalls()
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

        sut.forward(route, null)

        val result = sut.routes()
        assertEquals(listOf(route), result)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineTestScope.mock.default.withContext<Any>(any())
            coroutineTestScope.mock.default.withContext<Any>(any())
        }
    }

    @Test
    fun `forward with clearStack clears everything`() = coroutineTestScope.run {
        val route1 = NavigationRoute.Url("id-1", "url-1")
        val route2 = NavigationRoute.Url("id-2", "url-2")

        sut.forward(route1, null)
        coroutineTestScope.resetCalls()
        sut.forward(
            route = route2,
            navigationOptionObject = buildJsonObject {
                put("clear-stack", true)
            }
        )

        val result = sut.routes()
        assertEquals(listOf(route2), result)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineTestScope.mock.default.withContext<Any>(any())
            coroutineTestScope.mock.default.withContext<Any>(any())
        }
    }

    @Test
    fun `backward with Back removes last`() = coroutineTestScope.run {
        val route1 = NavigationRoute.Url("id-1", "url-1")
        val route2 = NavigationRoute.Url("id-2", "url-2")

        sut.forward(route1, null)
        sut.forward(route2, null)
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

        sut.forward(route1, null)
        sut.forward(route2, null)
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

        sut.forward(route1, null)
        coroutineTestScope.resetCalls()

        sut.forward(
            route = route2,
            navigationOptionObject = buildJsonObject {
                put("single", true)
            }
        )

        val result = sut.routes()
        assertEquals(listOf(route2), result)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineTestScope.mock.default.withContext<Any>(any())
            coroutineTestScope.mock.default.withContext<Any>(any())
        }
    }

    @Test
    fun `forward with invalid reuse throws`() = coroutineTestScope.run {
        val route = NavigationRoute.Url("id", "url")

        assertFailsWith<DomainException.Default> {
            sut.forward(
                route = route,
                navigationOptionObject = buildJsonObject {
                    put("reuse", "invalid")
                }
            )
        }

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineTestScope.mock.default.withContext<Any>(any())
        }
    }

    @Test
    fun `popupTo removes items until matching`() = coroutineTestScope.run {
        val route1 = NavigationRoute.Url("id-1", "url-1")
        val route2 = NavigationRoute.Url("id-2", "url-2")
        val route3 = NavigationRoute.Url("id-3", "url-3")

        sut.forward(route1, null)
        sut.forward(route2, null)
        sut.forward(route3, null)
        coroutineTestScope.resetCalls()

        sut.forward(
            route = NavigationRoute.Url("id", "url"),
            navigationOptionObject = buildJsonObject {
                put("pop-up-to", buildJsonObject {
                    put("url", "url-2")
                    put("inclusive", false)
                })
            }
        )

        val result = sut.routes()
        assertEquals(listOf(route1, route2, NavigationRoute.Url("id", "url")), result)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineTestScope.mock.default.withContext<Any>(any())
            coroutineTestScope.mock.default.withContext<Any>(any())
        }
    }

    @Test
    fun `forward with reuse last reuses last matching`() = coroutineTestScope.run {
        val routeInitial1 = NavigationRoute.Url("id-1", "url")
        val routeInitial2 = NavigationRoute.Url("id-2", "url")

        sut.forward(routeInitial1, null)
        sut.forward(routeInitial2, null)
        coroutineTestScope.resetCalls()

        val forwardResult = sut.forward(
            route = NavigationRoute.Url("id-X", "url"),
            navigationOptionObject = buildJsonObject {
                put("reuse", "last")
            }
        )

        assertNull(forwardResult)

        val result = sut.routes()
        assertEquals(listOf(routeInitial1, routeInitial2), result)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineTestScope.mock.default.withContext<Any>(any())
            coroutineTestScope.mock.default.withContext<Any>(any())
        }
    }

    @Test
    fun `forward with reuse first reuses first matching`() = coroutineTestScope.run {
        val routeInitial1 = NavigationRoute.Url("id-1", "url")
        val routeInitial2 = NavigationRoute.Url("id-2", "url")

        sut.forward(routeInitial1, null)
        sut.forward(routeInitial2, null)
        coroutineTestScope.resetCalls()

        val forwardResult = sut.forward(
            route = NavigationRoute.Url("id-X", "url"),
            navigationOptionObject = buildJsonObject {
                put("reuse", "first")
            }
        )

        assertNull(forwardResult)

        val result = sut.routes()
        assertEquals(listOf(routeInitial2, routeInitial1), result)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineTestScope.mock.default.withContext<Any>(any())
            coroutineTestScope.mock.default.withContext<Any>(any())
        }
    }

    @Test
    fun `forward with reuse true behaves as last`() = coroutineTestScope.run {
        val routeInitial1 = NavigationRoute.Url("id-1", "url")
        val routeInitial2 = NavigationRoute.Url("id-2", "url")

        sut.forward(routeInitial1, null)
        sut.forward(routeInitial2, null)
        coroutineTestScope.resetCalls()

        val forwardResult = sut.forward(
            route = NavigationRoute.Url("id-X", "url"),
            navigationOptionObject = buildJsonObject {
                put("reuse", "true")
            }
        )

        assertNull(forwardResult)

        val result = sut.routes()
        assertEquals(listOf(routeInitial1, routeInitial2), result)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineTestScope.mock.default.withContext<Any>(any())
            coroutineTestScope.mock.default.withContext<Any>(any())
        }
    }

    @Test
    fun `popupTo inclusive true removes including target`() = coroutineTestScope.run {
        val route1 = NavigationRoute.Url("id-1", "url-1")
        val route2 = NavigationRoute.Url("id-2", "url-2")
        val route3 = NavigationRoute.Url("id-3", "url-3")

        sut.forward(route1, null)
        sut.forward(route2, null)
        sut.forward(route3, null)
        coroutineTestScope.resetCalls()

        sut.forward(
            route = NavigationRoute.Url("id-X", "url-X"),
            navigationOptionObject = buildJsonObject {
                put("pop-up-to", buildJsonObject {
                    put("url", "url-2")
                    put("inclusive", true)
                })
            }
        )

        val result = sut.routes()
        assertEquals(listOf(route1, NavigationRoute.Url("id-X", "url-X")), result)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineTestScope.mock.default.withContext<Any>(any())
            coroutineTestScope.mock.default.withContext<Any>(any())
        }
    }

    @Test
    fun `popupTo greedy false uses last matching`() = coroutineTestScope.run {
        val routeFirst = NavigationRoute.Url("id-a", "shared")
        val routeSecond = NavigationRoute.Url("id-b", "shared")
        val routeThird = NavigationRoute.Url("id-c", "other")

        sut.forward(routeFirst, null)
        sut.forward(routeSecond, null)
        sut.forward(routeThird, null)
        coroutineTestScope.resetCalls()

        sut.forward(
            route = NavigationRoute.Url("id-X", "url-X"),
            navigationOptionObject = buildJsonObject {
                put("pop-up-to", buildJsonObject {
                    put("url", "shared")
                    put("greedy", false)
                })
            }
        )

        val result = sut.routes()
        assertEquals(listOf(routeFirst, routeSecond, NavigationRoute.Url("id-X", "url-X")), result)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineTestScope.mock.default.withContext<Any>(any())
            coroutineTestScope.mock.default.withContext<Any>(any())
        }
    }

    @Test
    fun `popupTo route not found throws`() = coroutineTestScope.run {
        val existingRoute = NavigationRoute.Url("id-1", "url-existing")
        sut.forward(existingRoute, null)
        coroutineTestScope.resetCalls()

        assertFailsWith<DomainException.Default> {
            sut.forward(
                route = NavigationRoute.Url("id-X", "url-X"),
                navigationOptionObject = buildJsonObject {
                    put("pop-up-to", buildJsonObject {
                        put("url", "missing-url")
                    })
                }
            )
        }

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineTestScope.mock.default.withContext<Any>(any())
        }
    }

    @Test
    fun `single removes all previous matching routes`() = coroutineTestScope.run {
        val routeFirst = NavigationRoute.Url("id-1", "url-shared")
        val routeSecond = NavigationRoute.Url("id-2", "url-shared")

        sut.forward(routeFirst, null)
        sut.forward(routeSecond, null)
        coroutineTestScope.resetCalls()

        sut.forward(
            route = NavigationRoute.Url("id-X", "url-shared"),
            navigationOptionObject = buildJsonObject {
                put("single", true)
            }
        )

        val result = sut.routes()
        assertEquals(listOf(NavigationRoute.Url("id-X", "url-shared")), result)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineTestScope.mock.default.withContext<Any>(any())
            coroutineTestScope.mock.default.withContext<Any>(any())
        }
    }

    @Test
    fun `forward returns null when route is reused`() = coroutineTestScope.run {
        val routeInitial = NavigationRoute.Url("id-1", "url")

        sut.forward(routeInitial, null)
        coroutineTestScope.resetCalls()

        val forwardResult = sut.forward(
            route = NavigationRoute.Url("id-X", "url"),
            navigationOptionObject = buildJsonObject {
                put("reuse", "last")
            }
        )

        assertNull(forwardResult)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineTestScope.mock.default.withContext<Any>(any())
        }
    }

    @Test
    fun `priorRoute returns previous route`() = coroutineTestScope.run {
        val firstRoute = NavigationRoute.Url("id-1", "url-1")
        val secondRoute = NavigationRoute.Url("id-2", "url-2")

        sut.forward(firstRoute, null)
        sut.forward(secondRoute, null)
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

        sut.forward(firstRoute, null)
        sut.forward(lastRoute, null)
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

        sut.forward(route1, null)
        sut.forward(route2, null)
        sut.backward(NavigationRoute.Back)
        sut.forward(route3, null)
        coroutineTestScope.resetCalls()

        val result = sut.routes()
        assertEquals(listOf(route1, route3), result)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineTestScope.mock.default.withContext<Any>(any())
        }
    }

    @Test
    fun `popupTo missing url throws`() = coroutineTestScope.run {
        assertFailsWith<DomainException.Default> {
            sut.forward(
                route = NavigationRoute.Url("id", "value"),
                navigationOptionObject = buildJsonObject {
                    put("pop-up-to", buildJsonObject {
                        // no url
                    })
                }
            )
        }

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineTestScope.mock.default.withContext<Any>(any())
        }
    }

    @Test
    fun `single missing keeps previous routes`() = coroutineTestScope.run {
        val route1 = NavigationRoute.Url("id-1", "url")
        val route2 = NavigationRoute.Url("id-2", "url")

        sut.forward(route1, null)
        coroutineTestScope.resetCalls()

        sut.forward(
            route = route2,
            navigationOptionObject = buildJsonObject { }
        )

        val result = sut.routes()
        assertEquals(listOf(route1, route2), result)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineTestScope.mock.default.withContext<Any>(any())
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
        val route1 = NavigationRoute.Url("id-1", "shared")
        val route2 = NavigationRoute.Url("id-2", "shared")
        val route3 = NavigationRoute.Url("id-3", "other")

        sut.forward(route1, null)
        sut.forward(route2, null)
        sut.forward(route3, null)
        coroutineTestScope.resetCalls()

        sut.forward(
            route = NavigationRoute.Url("id-X", "url-X"),
            navigationOptionObject = buildJsonObject {
                put("pop-up-to", buildJsonObject {
                    put("url", "shared")
                    put("inclusive", false)
                    put("greedy", true)
                })
            }
        )

        val result = sut.routes()
        assertEquals(listOf(route1, NavigationRoute.Url("id-X", "url-X")), result)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineTestScope.mock.default.withContext<Any>(any())
            coroutineTestScope.mock.default.withContext<Any>(any())
        }
    }

    @Test
    fun `forward reuse last does nothing when no matching route`() = coroutineTestScope.run {
        sut.forward(NavigationRoute.Url("id-1", "url-A"), null)
        coroutineTestScope.resetCalls()

        sut.forward(
            route = NavigationRoute.Url("id-2", "url-B"),
            navigationOptionObject = buildJsonObject {
                put("reuse", "last")
            }
        )

        val result = sut.routes()
        assertEquals(
            listOf(
                NavigationRoute.Url("id-1", "url-A"),
                NavigationRoute.Url("id-2", "url-B")
            ),
            result
        )

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineTestScope.mock.default.withContext<Any>(any())
            coroutineTestScope.mock.default.withContext<Any>(any())
        }
    }

    @Test
    fun `forward reuse first does nothing when no matching route`() = coroutineTestScope.run {
        val r1 = NavigationRoute.Url("id-1", "url-A")
        val r2 = NavigationRoute.Url("id-2", "url-B")

        sut.forward(r1, null)
        coroutineTestScope.resetCalls()

        sut.forward(
            route = r2,
            navigationOptionObject = buildJsonObject {
                put("reuse", "first")
            }
        )

        val result = sut.routes()

        assertEquals(
            listOf(
                NavigationRoute.Url("id-1", "url-A"),
                NavigationRoute.Url("id-2", "url-B")
            ),
            result
        )

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineTestScope.mock.default.withContext<Any>(any())
            coroutineTestScope.mock.default.withContext<Any>(any())
        }
    }

    @Test
    fun `forward with single false keeps previous matching`() = coroutineTestScope.run {
        val r1 = NavigationRoute.Url("id-1", "url")
        val r2 = NavigationRoute.Url("id-2", "url")

        sut.forward(r1, null)
        coroutineTestScope.resetCalls()

        sut.forward(
            route = r2,
            navigationOptionObject = buildJsonObject {
                put("single", false)
            }
        )

        val result = sut.routes()
        assertEquals(listOf(r1, r2), result)

        verifySuspend(VerifyMode.exhaustiveOrder) {
            coroutineTestScope.mock.default.withContext<Any>(any())
            coroutineTestScope.mock.default.withContext<Any>(any())
        }
    }
}
