package com.tezov.tuucho.core.domain.business.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class LanguageModelDomainTest {
    @Test
    fun `fromOrNull returns matching enum`() {
        val lang = LanguageModelDomain.fromOrNull("fr")
        assertEquals(LanguageModelDomain.French, lang)
    }

    @Test
    fun `fromOrNull returns null for unknown code`() {
        val lang = LanguageModelDomain.fromOrNull("es")
        assertNull(lang)
    }

    @Test
    fun `from returns matching enum`() {
        val lang = LanguageModelDomain.from("default")
        assertEquals(LanguageModelDomain.Default, lang)
    }

    @Test
    fun `from throws for unknown code`() {
        assertFailsWith<NoSuchElementException> {
            LanguageModelDomain.from("es")
        }
    }

    @Test
    fun `codes are exposed correctly`() {
        assertEquals("default", LanguageModelDomain.Default.code)
        assertEquals("fr", LanguageModelDomain.French.code)
    }
}
