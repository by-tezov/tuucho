package com.tezov.tuucho.core.domain.business.interaction.navigation.selector

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PageBreadCrumbNavigationDefinitionSelectorMatcherTest {
    @Test
    fun `accept returns false when breadcrumb shorter than values`() {
        val sut = PageBreadCrumbNavigationDefinitionSelectorMatcher(listOf("a", "b"))
        val result = sut.accept(listOf("b"))
        assertFalse(result)
    }

    @Test
    fun `accept returns true when breadcrumb equals values`() {
        val sut = PageBreadCrumbNavigationDefinitionSelectorMatcher(listOf("a", "b"))
        val result = sut.accept(listOf("a", "b"))
        assertTrue(result)
    }

    @Test
    fun `accept returns true when breadcrumb ends with values`() {
        val sut = PageBreadCrumbNavigationDefinitionSelectorMatcher(listOf("b", "c"))
        val result = sut.accept(listOf("a", "b", "c"))
        assertTrue(result)
    }

    @Test
    fun `accept returns false when breadcrumb does not end with values`() {
        val sut = PageBreadCrumbNavigationDefinitionSelectorMatcher(listOf("x", "y"))
        val result = sut.accept(listOf("a", "x", "z"))
        assertFalse(result)
    }

    @Test
    fun `accept with empty values always returns true`() {
        val sut = PageBreadCrumbNavigationDefinitionSelectorMatcher(emptyList())
        assertTrue(sut.accept(emptyList()))
        assertTrue(sut.accept(listOf("a", "b")))
    }

    @Test
    fun `accept with single value matches last element`() {
        val sut = PageBreadCrumbNavigationDefinitionSelectorMatcher(listOf("b"))
        assertTrue(sut.accept(listOf("a", "b")))
        assertFalse(sut.accept(listOf("a", "c")))
    }
}
