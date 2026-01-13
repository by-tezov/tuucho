package com.tezov.tuucho.core.domain.business.interaction.navigation

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class NavigationRouteTest {
    @Test
    fun `Back accept works`() {
        val backRoute = NavigationRoute.Back

        assertTrue(backRoute.accept(NavigationRoute.Back))
        assertFalse(backRoute.accept(NavigationRoute.Finish))
        assertFalse(backRoute.accept("back"))
    }

    @Test
    fun `Finish accept works`() {
        val finishRoute = NavigationRoute.Finish

        assertTrue(finishRoute.accept(NavigationRoute.Finish))
        assertFalse(finishRoute.accept(NavigationRoute.Back))
        assertFalse(finishRoute.accept("finish"))
    }

    @Test
    fun `Current accept works`() {
        val currentRoute = NavigationRoute.Current

        assertTrue(currentRoute.accept(NavigationRoute.Current))
        assertFalse(currentRoute.accept(NavigationRoute.Back))
        assertFalse(currentRoute.accept("current"))
    }

    @Test
    fun `Url accept works for Url`() {
        val route1 = NavigationRoute.Url("id-1", "value-shared")
        val route2 = NavigationRoute.Url("id-2", "value-shared")
        val routeDifferent = NavigationRoute.Url("id-3", "value-other")

        assertTrue(route1.accept(route2))
        assertFalse(route1.accept(routeDifferent))
    }

    @Test
    fun `Url accept works for String`() {
        val routeUrl = NavigationRoute.Url("id-1", "value-shared")

        assertTrue(routeUrl.accept("value-shared"))
        assertFalse(routeUrl.accept("value-other"))
    }

    @Test
    fun `Url accept returns false on unknown type`() {
        val routeUrl = NavigationRoute.Url("id-1", "value-shared")

        assertFalse(routeUrl.accept(42))
    }

    @Test
    fun `toString returns id for Back Finish Current`() {
        assertEquals("back", NavigationRoute.Back.toString())
        assertEquals("finish", NavigationRoute.Finish.toString())
        assertEquals("current", NavigationRoute.Current.toString())
    }

    @Test
    fun `toString returns value for Url`() {
        val routeUrl = NavigationRoute.Url("id-1", "value-shared")

        assertEquals("value-shared", routeUrl.toString())
    }
}
